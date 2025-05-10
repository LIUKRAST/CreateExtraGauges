package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.source.FactoryGaugeDisplaySource;
import net.createmod.catnip.data.IntAttached;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FactoryGaugeDisplaySource.class)
public abstract class FactoryGaugeDisplaySourceMixin extends DisplaySource {
    @Inject(
            method = "createEntry",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;getFilter()Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void createEntry(Level level, FactoryPanelPosition pos, CallbackInfoReturnable<IntAttached<MutableComponent>> cir, @Local FactoryPanelBehaviour panel) {
        if(panel instanceof AbstractPanelBehaviour ap) cir.setReturnValue(ap.getDisplayLinkComponent());
    }
}
