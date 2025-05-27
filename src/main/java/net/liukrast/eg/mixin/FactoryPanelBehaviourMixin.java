package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.util.IFPExtra;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(FactoryPanelBehaviour.class)
public abstract class FactoryPanelBehaviourMixin implements IFPExtra {

    @Unique
    private Map<BlockPos, FactoryPanelConnection> extra_gauges$targetedByExtra = new HashMap<>();

    @Override
    public Map<BlockPos, FactoryPanelConnection> extra_gauges$getExtra() {
        return extra_gauges$targetedByExtra;
    }

    @Shadow
    @Nullable
    public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelConnection connection) {
        throw new AssertionError("Mixin injection failed");
    }


    @Shadow public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
    @Shadow public boolean active;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo ci) {
        if(FactoryPanelBehaviour.class.cast(this) instanceof AbstractPanelBehaviour) ci.cancel();
    }

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

    @SuppressWarnings("ModifyVariableMayBeArgsOnly")
    @ModifyVariable(method = "moveTo", at = @At(value = "STORE", ordinal = 0))
    private FactoryPanelBehaviour moveTo(FactoryPanelBehaviour original) {
        var be = ((FactoryPanelBlockEntity)original.blockEntity);
        var slot = original.slot;
        if(be.panels.get(slot) instanceof AbstractPanelBehaviour superOriginal)
            return superOriginal.getPanelType().create(be, slot);
        return original;
    }

    @Inject(method = "moveTo", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", ordinal = 0), cancellable = true)
    private void moveTo(FactoryPanelPosition newPos, ServerPlayer player, CallbackInfo ci) {
        for(BlockPos pos : extra_gauges$targetedByExtra.keySet()) {
            if(!pos.closerThan(newPos.pos(), 24)) {
                ci.cancel();
                return;
            }
        }
    }

    @WrapWithCondition(
            method = "tickRequests",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;sendEffect(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;Z)V"
            )
    )
    private boolean tickRequests(FactoryPanelBehaviour instance, FactoryPanelPosition factoryPanelPosition, boolean fromPos) {
        return !(instance instanceof AbstractPanelBehaviour ab) || ab.hasConnection(EGPanelConnections.FILTER);
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putUUID(Ljava/lang/String;Ljava/util/UUID;)V"))
    private void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        panelTag.put("TargetedByExtra", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(extra_gauges$targetedByExtra.values())).orElseThrow());
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        extra_gauges$targetedByExtra.clear();
        CatnipCodecUtils.decode(Codec.list(FactoryPanelConnection.CODEC), panelTag.get("TargetedByExtra")).orElse(List.of())
                .forEach(c -> extra_gauges$targetedByExtra.put(c.from.pos(), c));
    }

    @Inject(method = "addConnection", at = @At("HEAD"), cancellable = true)
    private void addConnection(FactoryPanelPosition fromPos, CallbackInfo ci) {
        var i = FactoryPanelBehaviour.class.cast(this);
        var at = EGPanelConnections.getCap(i.getWorld(), fromPos.pos(), EGPanelConnections.REDSTONE);
        if(at == null) return;
        extra_gauges$targetedByExtra.put(fromPos.pos(), new FactoryPanelConnection(fromPos, 1));
        ci.cancel();
    }

    @Definition(id = "failed", local = @Local(type = boolean.class))
    @Expression("failed = @(true)")
    @ModifyExpressionValue(method = "tickRequests", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int tickRequests$1(int original) {
        var instance = FactoryPanelBehaviour.class.cast(this);
        return (!(instance instanceof AbstractPanelBehaviour ab) || ab.hasConnection(EGPanelConnections.FILTER)) ? 1 : 0;
    }

    @ModifyVariable(method = "checkForRedstoneInput", at = @At("STORE"))
    private boolean checkForRedstoneInput(boolean shouldPower, @Cancellable CallbackInfo ci) {
        var i = FactoryPanelBehaviour.class.cast(this);
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!i.getWorld().isLoaded(connection.from.pos())) {
                ci.cancel();
                return false;
            }
            FactoryPanelBehaviour behaviour = at(i.getWorld(), connection);
            if(behaviour == null || !behaviour.isActive() || !(behaviour instanceof AbstractPanelBehaviour panel)) {
                ci.cancel();
                return false;
            }
            if(panel.hasConnection(EGPanelConnections.REDSTONE)) {
                if(panel.hasConnection(EGPanelConnections.FILTER)) {
                    if(panel.shouldUseRedstoneInsteadOfFilter()) shouldPower |= panel.getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
                } else {
                    shouldPower |= panel.getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
                }
            }
        }
        for(FactoryPanelConnection connection : extra_gauges$targetedByExtra.values()) {
            var pos = connection.from.pos();
            if(!i.getWorld().isLoaded(pos)) {
                ci.cancel();
                return false;
            }
            var redstoneData = EGPanelConnections.getCap(i.getWorld(), pos, EGPanelConnections.REDSTONE);
            if(redstoneData != null) shouldPower |= redstoneData > 0;
        }
        return shouldPower;
    }

    @Inject(method = "notifyRedstoneOutputs", at = @At("TAIL"))
    private void notifyRedstoneOutputs(CallbackInfo ci) {
        //TODO: Implement for future outputs
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

    @ModifyExpressionValue(method = "onShortInteract", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I"))
    private int onShortInteract(int original) {
        return original + extra_gauges$targetedByExtra.size();
    }

    @ModifyExpressionValue(method = "onShortInteract", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<FactoryPanelConnection> onShortInteract(Collection<FactoryPanelConnection> original) {
        return Stream.concat(original.stream(), extra_gauges$targetedByExtra.values().stream()).collect(Collectors.toSet());
    }

    @Inject(method = "disconnectAllLinks", at = @At("TAIL"))
    private void disconnectAllLinks(CallbackInfo ci) {
        extra_gauges$targetedByExtra.clear();
    }
}
