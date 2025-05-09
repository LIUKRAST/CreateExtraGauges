package net.liukrast.eg.api.logistics.board;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.codecs.CatnipCodecs;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.logistics.box.EmptyValueBoxTransform;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.mixin.FilteringBehaviourMixin;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <h1>Abstract Panel Behaviour</h1>
 * Allows to create custom panel behaviors<br>
 * */
public abstract class AbstractPanelBehaviour extends FactoryPanelBehaviour {
    private final PanelType<?> type;
    /**
     * Constructor with the possibility to change the value box.
     * */
    @SuppressWarnings("unused")
    public AbstractPanelBehaviour(ValueBoxTransform valueBoxTransform, PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        this(type, be, slot);
        ((FilteringBehaviourMixin)this).setValueBoxTransform(new EmptyValueBoxTransform());
    }

    public AbstractPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(be, slot);
        this.type = type;
    }

    /**
     * Invoked on client tick when we are looking at the block.
     * */
    @OnlyIn(Dist.CLIENT)
    public void hoverTick() {}

    /**
     * Since original class extends {@link com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour},
     * return true if you want this gauge to have the render from filtering behaviour.
     * */
    public boolean shouldAllowFilteringBehaviour() {
        return false;
    }

    /**
     * @return The item associated with this behaviour. Used for drops and more.
     * */
    public abstract Item getItem();

    /**
     * @return Whether another gauge can connect with this one. Remember that {@link AbstractPanelBehaviour} is an instance of {@link FactoryPanelBehaviour}.
     * For instance, the default gauge cannot connect with another if their item filters are empty.
     * */
    public abstract boolean mayConnect(FactoryPanelBehaviour other);

    /**
     * @return the model for your custom gauge. Will automatically be used for rendering.
     * */
    public abstract PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType);

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
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        CompoundTag special = nbt.contains("CustomPanels") ? nbt.getCompound("CustomPanels") : new CompoundTag();
        special.putString(CreateLang.asId(slot.name()), Objects.requireNonNull(GaugeRegistry.PANEL_REGISTRY.getKey(type)).toString());
        nbt.put("CustomPanels", special);
        //We avoid adding some of the data from the original behaviour.
        if (!active)
            return;

        CompoundTag panelTag = new CompoundTag();
        super.write(panelTag, registries, clientPacket);

        panelTag.putBoolean("Satisfied", satisfied);
        panelTag.putBoolean("PromisedSatisfied", promisedSatisfied);
        panelTag.putBoolean("RedstonePowered", redstonePowered);
        panelTag.put("Targeting", CatnipCodecUtils.encode(CatnipCodecs.set(FactoryPanelPosition.CODEC), targeting).orElseThrow());
        panelTag.put("TargetedBy", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(targetedBy.values())).orElseThrow());
        panelTag.put("TargetedByLinks", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(targetedByLinks.values())).orElseThrow());

        nbt.put(CreateLang.asId(slot.name()), panelTag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {}

    @Override
    public boolean canShortInteract(ItemStack toApply) {
        return shouldAllowFilteringBehaviour() && super.canShortInteract(toApply);
    }
}
