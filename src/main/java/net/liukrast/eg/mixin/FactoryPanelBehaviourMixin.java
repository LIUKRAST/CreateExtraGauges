package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FactoryPanelBehaviour.class)
public class FactoryPanelBehaviourMixin {

    /* We don't want our panels to tick the default panel logic */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo ci) {extra_gauge$cancel(ci);}
    @Inject(method = "lazyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;lazyTick()V", shift = At.Shift.AFTER), cancellable = true)
    private void lazyTick(CallbackInfo ci) {extra_gauge$cancel(ci);}

    /* We want the class to safely handle null values in the panel slot */
    @Definition(id = "behaviour", local = @Local(type = FactoryPanelBehaviour.class))
    @Definition(id = "active", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;active:Z")
    @Expression("behaviour.active")
    @WrapOperation(
            method = "at(Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;)Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;",
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean at(FactoryPanelBehaviour instance, Operation<Boolean> original) {
        if(instance == null) return true;
        return original.call(instance);
    }

    @Unique
    private void extra_gauge$cancel(CallbackInfo ci) {
        if(FactoryPanelBehaviour.class.cast(this) instanceof AbstractPanelBehaviour) ci.cancel();
    }
}
