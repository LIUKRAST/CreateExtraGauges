package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.util.IFPExtra;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(FactoryPanelBehaviour.class)
public abstract class FactoryPanelBehaviourMixin implements IFPExtra {
    //Width for larger recipes
    @Unique private int extra_gauges$width = 3;
    //Map of extra sources, like levers and so on
    @Unique private final Map<BlockPos, FactoryPanelConnection> extra_gauges$targetedByExtra = new HashMap<>();

    /* SHADOWS */
    @Shadow public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
    @Shadow public boolean active;
    @Shadow
    @Nullable public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelConnection connection) {throw new AssertionError("Mixin injection failed");}
    @Shadow public abstract boolean isActive();

    /* INTERFACE METHODS */
    @Override public int extra_gauges$getWidth() {return extra_gauges$width;}
    @Override public void extra_gauges$setWidth(int width) {extra_gauges$width = width;}
    @Override public Map<BlockPos, FactoryPanelConnection> extra_gauges$getExtra() {return extra_gauges$targetedByExtra;}

    /* Allows abstract panels to decide whether they want to use or original tick function */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo ci) {if(FactoryPanelBehaviour.class.cast(this) instanceof AbstractPanelBehaviour ab && ab.skipOriginalTick()) ci.cancel();}

    @Definition(id = "behaviour", local = @Local(type = FactoryPanelBehaviour.class))
    @Definition(id = "active", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;active:Z") @Expression("behaviour.active")
    @WrapOperation(method = "at(Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;)Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean at(FactoryPanelBehaviour instance, Operation<Boolean> original) {
        if(instance == null) return true;
        return original.call(instance);
    }

    /* MOVE TO */
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

    /* TICK REQUESTS */
    /* In the tick requests we filter all the gauges that do not contain a filter connection to avoid ticking non-item-related gauges */
    @Redirect(method = "tickRequests", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;targetedBy:Ljava/util/Map;"))
    private Map<FactoryPanelPosition, FactoryPanelConnection> tickRequests(FactoryPanelBehaviour instance) {
        Map<FactoryPanelPosition, FactoryPanelConnection> filtered = new HashMap<>();
        block: for (Map.Entry<FactoryPanelPosition, FactoryPanelConnection> entry : instance.targetedBy.entrySet()) {
            FactoryPanelBehaviour source = FactoryPanelBehaviour.at(instance.getWorld(), entry.getValue());
            if(source instanceof AbstractPanelBehaviour ab) {
                for(var c : ab.getConnections()) {
                    if(c == EGPanelConnections.FILTER.get()) break;
                    if(c == EGPanelConnections.REDSTONE.get()) continue block;
                    if(c == EGPanelConnections.INTEGER.get()) continue block;
                    if(c == EGPanelConnections.STRING.get()) continue block;
                }
            }
            filtered.put(entry.getKey(), entry.getValue());
        }
        return filtered;
    }

    /* DATA */
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putUUID(Ljava/lang/String;Ljava/util/UUID;)V"))
    private void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        panelTag.put("TargetedByExtra", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(extra_gauges$targetedByExtra.values())).orElseThrow());
        if(extra_gauges$width != 3) panelTag.putInt("extra_gauges$CraftWidth", extra_gauges$width);
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        extra_gauges$targetedByExtra.clear();
        CatnipCodecUtils.decode(Codec.list(FactoryPanelConnection.CODEC), panelTag.get("TargetedByExtra")).orElse(List.of())
                .forEach(c -> extra_gauges$targetedByExtra.put(c.from.pos(), c));
        extra_gauges$width = panelTag.contains("extra_gauges$CraftWidth") ? panelTag.getInt("extra_gauges$CraftWidth") : 3;
    }

    /* ADDING/REMOVING CONNECTIONS */
    @Inject(method = "addConnection", at = @At("HEAD"), cancellable = true)
    private void addConnection(FactoryPanelPosition fromPos, CallbackInfo ci) {
        var i = FactoryPanelBehaviour.class.cast(this);
        var fromState = i.getWorld().getBlockState(fromPos.pos());
        if(PanelConnection.makeContext(i.getWorld().getBlockState(i.getPos())) == PanelConnection.makeContext(fromState) && EGRegistries.PANEL_CONNECTION_REGISTRY
                .stream()
                .map(c -> c.getListener(fromState.getBlock()))
                .anyMatch(Objects::nonNull)
        ) {
            extra_gauges$targetedByExtra.put(fromPos.pos(), new FactoryPanelConnection(fromPos, 1));
            i.blockEntity.notifyUpdate();
            ci.cancel();
        }
    }

    @Inject(method = "disconnectAllLinks", at = @At("TAIL"))
    private void disconnectAllLinks(CallbackInfo ci) {
        extra_gauges$targetedByExtra.clear();
    }

    /* OTHER PANELS UPDATE */
    @ModifyVariable(method = "checkForRedstoneInput", at = @At(value = "STORE", ordinal = 0))
    private boolean checkForRedstoneInput(boolean shouldPower, @Cancellable CallbackInfo ci) {
        var i = FactoryPanelBehaviour.class.cast(this);
        block: for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!i.getWorld().isLoaded(connection.from.pos())) {
                ci.cancel();
                return false;
            }
            Level world = i.getWorld();
            FactoryPanelBehaviour behaviour = at(world, connection);
            if(behaviour == null || !behaviour.isActive()) return false;
            if(!(behaviour instanceof AbstractPanelBehaviour panel)) continue;
            for(var c : panel.getConnections()) {
                if(c == EGPanelConnections.FILTER.get()) continue block;
                if(c == EGPanelConnections.INTEGER.get()) continue block;
                if(c == EGPanelConnections.REDSTONE.get()) {
                    shouldPower |= panel.getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
                    continue block;
                }
            }
        }
        for(var connection : extra_gauges$targetedByExtra.values()) {
            var pos = connection.from.pos();
            if(!i.getWorld().isLoaded(pos)) {
                ci.cancel();
                return false;
            }
            var state = i.getWorld().getBlockState(pos);
            var be = i.getWorld().getBlockEntity(pos);
            var listener = EGPanelConnections.REDSTONE.get().getListener(state.getBlock());
            if(listener == null) continue;
            var opt = listener.invalidate(i.getWorld(), state, pos, be);
            if(opt.isPresent()) shouldPower |= opt.get() > 0;
        }
        return shouldPower;
    }

    @Definition(id = "shouldPower", local = @Local(type = boolean.class))
    @Definition(id = "redstonePowered", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;redstonePowered:Z")
    @Expression("shouldPower == this.redstonePowered")
    @ModifyExpressionValue(method = "checkForRedstoneInput", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkForRedstoneInput$1(boolean original) {
        var i = FactoryPanelBehaviour.class.cast(this);
        Integer total = null;
        StringBuilder addressChange = null;
        block: for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!i.getWorld().isLoaded(connection.from.pos())) {
                return false;
            }
            Level world = i.getWorld();
            FactoryPanelBehaviour behaviour = at(world, connection);
            if(behaviour == null || !behaviour.isActive()) return false;
            if(!(behaviour instanceof AbstractPanelBehaviour panel)) continue;
            Set<PanelConnection<?>> connections = panel.getConnections();
            for(PanelConnection<?> c : connections) {
                if(c == EGPanelConnections.FILTER.get()) continue block;
                if(c == EGPanelConnections.INTEGER.get()) {
                    if(total == null) total = 0;
                    total += panel.getConnectionValue(EGPanelConnections.INTEGER.get()).orElse(0);
                    continue block;
                }
                if(c == EGPanelConnections.REDSTONE.get()) continue block;
                if(c == EGPanelConnections.STRING.get()) {
                    if(addressChange == null) addressChange = new StringBuilder(panel.getConnectionValue(EGPanelConnections.STRING.get()).orElse(""));
                    else addressChange.append(panel.getConnectionValue(EGPanelConnections.STRING.get()).orElse(""));
                    continue block;
                }
            }
        }
        for(var connection : extra_gauges$targetedByExtra.values()) {
            var pos = connection.from.pos();
            if(!i.getWorld().isLoaded(pos)) {
                return false;
            }
            var state = i.getWorld().getBlockState(pos);
            var be = i.getWorld().getBlockEntity(pos);
            var redstoneListener = EGPanelConnections.REDSTONE.get().getListener(state.getBlock());
            if(redstoneListener != null && redstoneListener.invalidate(i.getWorld(), state, pos, be).isPresent()) continue;
            var intListener = EGPanelConnections.INTEGER.get().getListener(state.getBlock());
            if(intListener != null) {
                var opt = intListener.invalidate(i.getWorld(), state, pos, be);
                if (opt.isPresent()) {
                    total += opt.get();
                    continue;
                }
            }
            var listener = EGPanelConnections.STRING.get().getListener(state.getBlock());
            if(listener == null) continue;
            var opt = listener.invalidate(i.getWorld(), state, pos, be);
            if(opt.isPresent()) {
                if(addressChange == null) addressChange = new StringBuilder(opt.get());
                else addressChange.append(opt.get());
            }
        }
        String fAddress = addressChange == null ? null : addressChange.toString();
        if((total == null || total == i.count) && (fAddress == null || fAddress.equals(i.recipeAddress))) return false;
        if(total != null) i.count = total;
        if(fAddress != null) i.recipeAddress = fAddress;
        return true;
    }

    @Inject(method = "notifyRedstoneOutputs", at = @At("TAIL"))
    private void notifyRedstoneOutputs(CallbackInfo ci) {
        // Implement for future outputs
    }

    /* INTERACTION */
    @ModifyExpressionValue(method = "onShortInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0))
    private boolean onShortInteract(boolean original) {
        var instance = FactoryPanelBehaviour.class.cast(this);
        return instance instanceof AbstractPanelBehaviour panel ? panel.withFilteringBehaviour() && original : original;
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
}
