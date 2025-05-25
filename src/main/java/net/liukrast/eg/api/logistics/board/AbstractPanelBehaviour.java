package net.liukrast.eg.api.logistics.board;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.codecs.CatnipCodecs;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.mixin.FactoryPanelBehaviourIMixin;
import net.liukrast.eg.mixin.FilteringBehaviourMixin;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * <h1>Abstract Panel Behaviour</h1>
 * Allows to create custom panel behaviors<br>
 * */
public abstract class AbstractPanelBehaviour extends FactoryPanelBehaviour {
    private final PanelType<?> type;
    private final Map<PanelConnection<?>, Supplier<?>> connections = new Reference2ObjectArrayMap<>();

    public AbstractPanelBehaviour(ValueBoxTransform valueBoxTransform, PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        this(type, be, slot);
        ((FilteringBehaviourMixin)this).setValueBoxTransform(valueBoxTransform);
    }

    public AbstractPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(be, slot);
        var builder = new PanelConnectionBuilder();
        addConnections(builder);
        connections.putAll(builder.map);
        this.type = type;
    }

    /**
     * Adds new connections to the panel. See more in {@link PanelConnection}
     * */
    public abstract void addConnections(PanelConnectionBuilder builder);

    /**
     * Please use {@link PanelConnections}
     * */
    public <T> Optional<T> getConnectionValue(PanelConnection<T> panelConnection) {
        if(!connections.containsKey(panelConnection)) return Optional.empty();
        // We can safely cast here.
        //noinspection unchecked
        return Optional.ofNullable((T) connections.get(panelConnection).get());
    }

    /**
     * @return Whether this behavior has a precise connection
     * */
    public boolean hasConnection(PanelConnection<?> connection) {
        return connections.containsKey(connection);
    }

    /**
     * @param shortenNumbers whether the display is in mode "shortened" or "full_number"
     * @return The component for display links
     * */
    public MutableComponent getDisplayLinkComponent(boolean shortenNumbers) {
        return Component.empty();
    }

    public Map<PanelConnection<?>, Supplier<?>> getConnections() {
        return connections;
    }

    public static class PanelConnectionBuilder {
        private final Map<PanelConnection<?>, Supplier<?>> map = new HashMap<>();

        private PanelConnectionBuilder() {}

        @SuppressWarnings("UnusedReturnValue")
        public <T> PanelConnectionBuilder put(@NotNull PanelConnection<T> panelConnection, @NotNull Supplier<T> getter) {
            map.put(panelConnection, getter);
            return this;
        }

    }

    /**
     * @return Whether the panel should render its bulb
     * */
    public boolean shouldRenderBulb() {
        return false;
    }

    /**
     * Since original class extends {@link com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour},
     * return true if you want this gauge to have the render from filtering behavior.
     * */
    public boolean shouldAllowFilteringBehaviour() {
        return false;
    }

    /**
     * @return The item associated with this behavior. Used for drops and more.
     * */
    public abstract Item getItem();

    /**
     * @return the model for your custom gauge. Will automatically be used for rendering.
     * */
    public abstract PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType);

    /**
     * An easier extension of {@link AbstractPanelBehaviour#write(CompoundTag, HolderLookup.Provider, boolean)}.
     * @param nbt The compound tag of the single gauge slot. Save your data into this
     * */
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}

    /**
     * An easier extension of {@link AbstractPanelBehaviour#read(CompoundTag, HolderLookup.Provider, boolean)}.
     * @param nbt The compound tag of the single gauge slot. Read your data from this slot
     * */
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {}

    /**
     * Used for abstract panels which have both {@link PanelConnections#FILTER} & {@link PanelConnections#REDSTONE}.
     * In those cases, the factory gauge will try to interact with them based on this value:
     * If the return value is true,
     * it will get the filter information from your custom gauge and update their recipe and so on.
     * Else, if the return value is false,
     * it will use the redstone information from your custom gauge and update their blocked/unblocked state.
     * */
    public boolean shouldUseRedstoneInsteadOfFilter() {
        return false;
    }

    public PanelType<?> getPanelType() {
        return type;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
        if(blockEntity instanceof FactoryPanelBlockEntity be) {
            var newBehaviour = new FactoryPanelBehaviour(be, this.slot);
            newBehaviour.active = false;
            blockEntity.attachBehaviourLate(newBehaviour);
            be.panels.put(slot, newBehaviour);
            be.redraw = true;
            be.lastShape = null;
            be.notifyUpdate();
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        //TODO: Should we avoid calling super?
        super.read(nbt, registries, clientPacket);
        CompoundTag panelTag = nbt.getCompound(CreateLang.asId(slot.name()));
        if (panelTag.isEmpty()) {
            active = false;
            return;
        }
        easyRead(panelTag, registries, clientPacket);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        CompoundTag special = nbt.contains("CustomPanels") ? nbt.getCompound("CustomPanels") : new CompoundTag();
        special.putString(CreateLang.asId(slot.name()), Objects.requireNonNull(GaugeRegistry.PANEL_REGISTRY.getKey(type)).toString());
        nbt.put("CustomPanels", special);
        //We avoid adding some data that is pointless in a generic gauge.
        // You can re-add it in your custom write method though
        //NOTE: If you feel like some data should not be avoided, please open a GitHub issue to report this.
        // This API is still very WIP and support is very well accepted
        if (!active)
            return;

        CompoundTag panelTag = new CompoundTag();
        panelTag.putBoolean("Satisfied", satisfied);
        panelTag.putBoolean("PromisedSatisfied", promisedSatisfied);
        panelTag.putBoolean("RedstonePowered", redstonePowered);
        panelTag.put("Targeting", CatnipCodecUtils.encode(CatnipCodecs.set(FactoryPanelPosition.CODEC), targeting).orElseThrow());
        panelTag.put("TargetedBy", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(targetedBy.values())).orElseThrow());
        panelTag.put("TargetedByLinks", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(targetedByLinks.values())).orElseThrow());

        easyWrite(panelTag, registries, clientPacket);

        nbt.put(CreateLang.asId(slot.name()), panelTag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new BasicPanelScreen(this));
    }

    @Override
    public boolean canShortInteract(ItemStack toApply) {
        return shouldAllowFilteringBehaviour() && super.canShortInteract(toApply);
    }

    @Override
    public ItemStack getFilter() {
        return getConnectionValue(PanelConnections.FILTER).orElse(ItemStack.EMPTY);
    }

    // We invoke the private function through mixin. Create, why are you making this method private...
    public void notifyRedstoneOutputs() {
        ((FactoryPanelBehaviourIMixin)this).extra_gauges$notifyRedstoneOutputs();
    }

    @Override
    public boolean acceptsValueSettings() {
        return true;
    }
}
