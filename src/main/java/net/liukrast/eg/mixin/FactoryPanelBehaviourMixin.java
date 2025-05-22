package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(FactoryPanelBehaviour.class)
public abstract class FactoryPanelBehaviourMixin {

    @Shadow
    @Nullable
    public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelConnection connection) {
        throw new AssertionError("Mixin injection failed");
    }

    @Shadow
    @Nullable
    public static FactoryPanelSupportBehaviour linkAt(BlockAndTintGetter world, FactoryPanelConnection connection) {
        throw new AssertionError("Mixin injection failed");
    }


    @Shadow public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
    @Shadow public boolean active;
    @Shadow public Map<BlockPos, FactoryPanelConnection> targetedByLinks;
    @Shadow public boolean redstonePowered;
    @Shadow private int timer;

    /* We don't want our panels to tick the default panel logic */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo ci) {extra_gauge$cancel(ci);}
    /*
    @Inject(method = "lazyTick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;lazyTick()V", shift = At.Shift.AFTER), cancellable = true)
    private void lazyTick(CallbackInfo ci) {extra_gauge$cancel(ci);}*/

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
    @ModifyVariable(method = "moveTo", at = @At(value = "STORE", ordinal = 0))
    private FactoryPanelBehaviour moveTo(FactoryPanelBehaviour original) {
        var be = ((FactoryPanelBlockEntity)original.blockEntity);
        var slot = original.slot;
        if(be.panels.get(slot) instanceof AbstractPanelBehaviour superOriginal)
            return superOriginal.getPanelType().create(be, slot);
        return original;
    }

    @WrapWithCondition(
            method = "tickRequests",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;sendEffect(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;Z)V"
            )
    )
    private boolean tickRequests(FactoryPanelBehaviour instance, FactoryPanelPosition factoryPanelPosition, boolean fromPos) {
        return !(instance instanceof AbstractPanelBehaviour ab) || ab.hasConnection(PanelConnections.FILTER);
    }

    @Definition(id = "failed", local = @Local(type = boolean.class))
    @Expression("failed = @(true)")
    @ModifyExpressionValue(method = "tickRequests", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int tickRequests$1(int original) {
        var instance = FactoryPanelBehaviour.class.cast(this);
        return (!(instance instanceof AbstractPanelBehaviour ab) || ab.hasConnection(PanelConnections.FILTER)) ? 1 : 0;
    }

    //TODO: Might be a bad idea to rewrite the whole code.
    // But I didnt find any other solution and my brain is collapsing ngl
    @Inject(method = "checkForRedstoneInput", at = @At("HEAD"), cancellable = true)
    private void checkForRedstoneInput(CallbackInfo ci) {
        var i = FactoryPanelBehaviour.class.cast(this);
        if (!active) {
            ci.cancel();
            return;
        }

        boolean shouldPower = false;
        //Injected code
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!i.getWorld().isLoaded(connection.from.pos())) {
                ci.cancel();
                return;
            }
            FactoryPanelBehaviour behaviour = at(i.getWorld(), connection);
            if(behaviour == null || !behaviour.isActive() || !(behaviour instanceof AbstractPanelBehaviour panel)) {
                ci.cancel();
                return;
            }
            if(panel.hasConnection(PanelConnections.REDSTONE)) {
                if(panel.hasConnection(PanelConnections.FILTER)) {
                    if(panel.shouldUseRedstoneInsteadOfFilter()) shouldPower |= panel.getConnectionValue(PanelConnections.REDSTONE).orElse(0) > 0;
                } else {
                    shouldPower |= panel.getConnectionValue(PanelConnections.REDSTONE).orElse(0) > 0;
                }
            }
        }

        for (FactoryPanelConnection connection : targetedByLinks.values()) {
            if (!i.getWorld().isLoaded(connection.from.pos())) {
                ci.cancel();
                return;
            }
            FactoryPanelSupportBehaviour linkAt = linkAt(i.getWorld(), connection);
            if (linkAt == null) {
                ci.cancel();
                return;
            }
            shouldPower |= linkAt.shouldPanelBePowered();
        }

        //noinspection ConstantValue
        if (shouldPower == redstonePowered) {
            ci.cancel();
            return;
        }

        redstonePowered = shouldPower;
        i.blockEntity.notifyUpdate();
        timer = 1;
        ci.cancel();
    }

    // TODO: Should we implement something here?
    @Inject(method = "notifyRedstoneOutputs", at = @At("TAIL"))
    private void notifyRedstoneOutputs(CallbackInfo ci) {
    }

    @ModifyExpressionValue(method = "onShortInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0))
    private boolean onShortInteract(boolean original) {
        var instance = FactoryPanelBehaviour.class.cast(this);
        return instance instanceof AbstractPanelBehaviour panel ? panel.shouldAllowFilteringBehaviour() && original : original;
    }

    @Definition(id = "heldItem", local = @Local(type = ItemStack.class))
    @Definition(id = "getItem", method = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    @Definition(id = "LogisticallyLinkedBlockItem", type = LogisticallyLinkedBlockItem.class)
    @Expression("heldItem.getItem() instanceof LogisticallyLinkedBlockItem")
    @ModifyExpressionValue(method = "onShortInteract", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean onShortInteract$1(boolean original) {
        var instance = FactoryPanelBehaviour.class.cast(this);
        return original && !(instance instanceof AbstractPanelBehaviour);
    }
}
