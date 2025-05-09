package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.events.ClientEvents;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEvents.class)
public class ClientEventsMixin {

    // We want to dispatch extra rendering for abstract panel behaviours
    @Inject(method = "onTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringRenderer;tick()V", shift = At.Shift.AFTER))
    private static void onTick(boolean isPreEvent, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (!(target instanceof BlockHitResult result)) return;
        ClientLevel world = mc.level;
        if(world == null) return;
        BlockPos pos = result.getBlockPos();
        if(mc.player == null) return;
        if (mc.player.isShiftKeyDown()) return;
        for(PanelSlot panelSlot : PanelSlot.values()) {
            var at = FactoryPanelBehaviour.at(world, new FactoryPanelPosition(pos, panelSlot));
            if(at instanceof AbstractPanelBehaviour abstractPanel) abstractPanel.hoverTick();
        }
    }
}
