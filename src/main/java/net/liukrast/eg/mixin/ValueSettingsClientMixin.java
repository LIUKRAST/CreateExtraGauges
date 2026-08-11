package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.liukrast.eg.content.logistics.link.LinkedControlBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ValueSettingsClient.class)
public class ValueSettingsClientMixin {
    @Shadow private Minecraft mc;

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsInputHandler;canInteract(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean canInteract(boolean original, @Local(name = "player") Player player) {
        return createExtraGauges$canInteract(original, mc, player);
    }

    @ModifyExpressionValue(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsInputHandler;canInteract(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean canInteract$1(boolean original,  @Local(name = "mc") Minecraft mc) {
        return createExtraGauges$canInteract(original, mc, mc.player);
    }

    @Unique
    private static boolean createExtraGauges$canInteract(boolean original, Minecraft mc, Player player) {
        Level level = mc.level;
        Block block = null;

        if (level != null && mc.hitResult instanceof BlockHitResult hitResult) {
            BlockPos pos = hitResult.getBlockPos();
            block = level.getBlockState(pos).getBlock();
        }

        return block instanceof LinkedControlBlock
                ? player != null && !player.isSpectator() && !AdventureUtil.isAdventure(player)
                : original;
    }
}
