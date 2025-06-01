package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(FactoryPanelConnectionHandler.class)
public class FactoryPanelConnectionHandlerMixin {
    @Shadow
    static FactoryPanelPosition connectingFrom;

    @Shadow
    static AABB connectingFromBox;

    @Inject(
            method = "checkForIssues(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;)Ljava/lang/String;",
            at = @At("RETURN"),
            cancellable = true)
    private static void checkForIssues(FactoryPanelBehaviour from, FactoryPanelBehaviour to, CallbackInfoReturnable<String> cir) {
        @Nullable String returnValue = cir.getReturnValue();
        if(
                returnValue != null
                        && (returnValue.equals("factory_panel.no_item") || returnValue.equals("factory_panel.input_in_restock_mode"))
                        //TODO: ALLOW GAUGES TO CONNECT UNDER SPECIAL CONDITIONS && true
        ) {
            cir.setReturnValue(null);
        }
    }

    @ModifyArg(
            method = "panelClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private static Component panelClicked(Component chatComponent, @Local(ordinal = 0, argsOnly = true) FactoryPanelBehaviour panel, @Local(ordinal = 1) FactoryPanelBehaviour at) {
        if(!(panel instanceof AbstractPanelBehaviour) && !(at instanceof AbstractPanelBehaviour)) return chatComponent;
        return Component.translatable("extra_gauges.panel.panels_connected");
    }

    @Inject(
            method = "onRightClick",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;get(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;"),
            cancellable = true
    )
    private static void onRightClick(CallbackInfoReturnable<Boolean> cir, @Local Minecraft mc, @Local BlockHitResult bhr) {
        assert mc.level != null;
        var state = mc.level.getBlockState(bhr.getBlockPos());
        if(EGRegistries.PANEL_CONNECTION_REGISTRY
                .stream()
                .map(c -> mc.level.getCapability(c.asCapability(), bhr.getBlockPos(), PanelConnection.PanelContext.from(state)))
                .anyMatch(Objects::nonNull)
        ) {
            FactoryPanelBehaviour at = FactoryPanelBehaviour.at(mc.level, connectingFrom);
            String checkForIssues = extra_gauges$checkForSpecialIssues(at, bhr.getBlockPos(), mc.level.getBlockState(bhr.getBlockPos()));
            if (checkForIssues != null) {
                assert mc.player != null;
                mc.player.displayClientMessage(CreateLang.translate(checkForIssues)
                        .style(ChatFormatting.RED)
                        .component(), true);
                connectingFrom = null;
                connectingFromBox = null;
                AllSoundEvents.DENY.playAt(mc.level, mc.player.blockPosition(), 1, 1, false);
                cir.setReturnValue(true);
                return;
            }
            FactoryPanelPosition bestPosition = null;
            double bestDistance = Double.POSITIVE_INFINITY;

            for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
                FactoryPanelPosition panelPosition = new FactoryPanelPosition(bhr.getBlockPos(), slot);
                FactoryPanelConnection connection = new FactoryPanelConnection(panelPosition, 1);
                Vec3 diff =
                        connection.calculatePathDiff(mc.level.getBlockState(connectingFrom.pos()), connectingFrom);
                if (bestDistance < diff.lengthSqr())
                    continue;
                bestDistance = diff.lengthSqr();
                bestPosition = panelPosition;
            }

            CatnipServices.NETWORK.sendToServer(new FactoryPanelConnectionPacket(bestPosition, connectingFrom, false));

            assert mc.player != null;
            mc.player.displayClientMessage(CreateLang
                    .translate("factory_panel.link_connected", mc.level.getBlockState(bhr.getBlockPos())
                            .getBlock()
                            .getName())
                    .style(ChatFormatting.GREEN)
                    .component(), true);

            connectingFrom = null;
            connectingFromBox = null;
            mc.player.level()
                    .playLocalSound(mc.player.blockPosition(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS,
                            0.5f, 0.5f, false);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static String extra_gauges$checkForSpecialIssues(FactoryPanelBehaviour from, BlockPos toPos, BlockState to) {
        if (from == null)
            return "factory_panel.connection_aborted";

        BlockState state1 = from.blockEntity.getBlockState();
        BlockPos diff = toPos
                .subtract(from.getPos());
        Direction connectedDirection = FactoryPanelBlock.connectedDirection(state1);

        if (connectedDirection != to.getOptionalValue(WrenchableDirectionalBlock.FACING)
                .orElse(connectedDirection))
            return "factory_panel.same_orientation";

        if (connectedDirection.getAxis()
                .choose(diff.getX(), diff.getY(), diff.getZ()) != 0)
            return "factory_panel.same_surface";

        if (!diff.closerThan(BlockPos.ZERO, 16))
            return "factory_panel.too_far_apart";

        return null;
    }
}
