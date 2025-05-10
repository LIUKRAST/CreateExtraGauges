package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FactoryPanelConnectionHandler.class)
public class FactoryPanelConnectionHandlerMixin {
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
}
