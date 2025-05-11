package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @ModifyArg(
            method = "panelClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private static Component panelClicked(Component chatComponent, @Local(ordinal = 0, argsOnly = true) FactoryPanelBehaviour panel, @Local(ordinal = 1) FactoryPanelBehaviour at) {
        if(!(panel instanceof AbstractPanelBehaviour) && !(at instanceof AbstractPanelBehaviour)) return chatComponent;
        return Component.translatable("extra_gauges.panel.panels_connected");
    }
}
