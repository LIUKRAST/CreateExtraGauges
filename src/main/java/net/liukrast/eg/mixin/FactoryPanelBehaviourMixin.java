package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
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

    @SuppressWarnings("ModifyVariableMayBeArgsOnly")
    @ModifyVariable(method = "moveTo", at = @At(value = "STORE"))
    private FactoryPanelBehaviour moveTo(FactoryPanelBehaviour original) {
        var be = ((FactoryPanelBlockEntity)original.blockEntity);
        var slot = original.slot;
        if(be.panels.get(slot) instanceof AbstractPanelBehaviour superOriginal)
            return superOriginal.getPanelType().create(be, slot);
        return original;
    }

    //TODO: Should we remove the shortInteraction? We will see if after release there are bugs with that.



    // Remove abstract panels from failing a request
    /*@Definition(id = "failed", local = @Local(type = boolean.class))
    @Expression("failed = true")
    @WrapWithCondition(method = "tickRequests", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tickRequests() {
        return extra_gauges$shouldSkipFilter();
    }*/

    //TODO: Replace with a better operation!
    @Unique
    private boolean extra_gauges$failed;

    @WrapWithCondition(
            method = "tickRequests",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;sendEffect(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;Z)V", ordinal = 0)
    )
    private boolean tickRequests(FactoryPanelBehaviour instance, FactoryPanelPosition factoryPanelPosition, boolean fromPos, @Local boolean failed) {
        boolean should = !extra_gauges$shouldSkipFilter() || failed;
        extra_gauges$failed = should;
        return should;
    }

    @Definition(id = "failed", local = @Local(type = boolean.class))
    @Expression("failed = true")
    @Inject(method = "tickRequests", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void tickRequests(CallbackInfo ci, @Local LocalBooleanRef failed) {
        failed.set(this.extra_gauges$failed);
    }


    @Unique
    private boolean extra_gauges$shouldSkipFilter() {
        if(FactoryPanelBehaviour.class.cast(this) instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.hasConnection(PanelConnections.FILTER);
        return true;
    }

}
