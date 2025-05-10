package net.liukrast.eg.mixin;

import com.simibubi.create.foundation.events.ClientEvents;
import net.liukrast.eg.content.logistics.logicBoard.LogicPanelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEvents.class)
public class ClientEventsMixin {

    @Inject(method = "onTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringRenderer;tick()V", shift = At.Shift.AFTER))
    private static void onTick(boolean isPreEvent, CallbackInfo ci) {
        LogicPanelRenderer.tick();
    }
}
