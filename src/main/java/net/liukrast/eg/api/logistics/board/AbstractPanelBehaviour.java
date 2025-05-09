package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.logistics.box.EmptyValueBoxTransform;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.mixin.FilteringBehaviourMixin;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

/**
 * <h1>Abstract Panel Behaviour</h1>
 * Allows to create custom panel behaviors<br>
 * Since {@link FactoryPanelBehaviour} extends {@link com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour},
 * we will need a second behavior to make modifiable options.
 * */
public abstract class AbstractPanelBehaviour extends FactoryPanelBehaviour {
    private final PanelType<?> type;

    public AbstractPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(be, slot);
        ((FilteringBehaviourMixin)this).setValueBoxTransform(new EmptyValueBoxTransform());
        this.type = type;
    }

    public abstract Item getItem();

    public abstract PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType);

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {

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


    //TODO: REDO THIS!
    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        try {
            CompoundTag special = nbt.contains("CustomPanels") ? nbt.getCompound("CustomPanels") : new CompoundTag();
            if (!active) return;
            //Save Custom panel data

            special.putString(CreateLang.asId(slot.name()), Objects.requireNonNull(GaugeRegistry.PANEL_REGISTRY.getKey(type)).toString());
            nbt.put("CustomPanels", special);
            //Save your data from here

            CompoundTag panelTag = new CompoundTag();
            //TODO: ADD DEFAULT DATA!
            nbt.put(CreateLang.asId(slot.name()), panelTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: REDO THIS!
    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        CompoundTag panelTag = nbt.getCompound(CreateLang.asId(slot.name()));
        if(panelTag.isEmpty()) {
            active = false;
            return;
        }

        //TODO: ADD DEFAULT DATA!
    }
}
