package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.mixin.FactoryPanelBehaviourAccessor;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PassivePanelBehaviour extends AbstractPanelBehaviour {
    public PassivePanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.FILTER, () -> filter);
    }

    @Override
    public Item getItem() {
        return EGItems.PASSIVE_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.PASSIVE_PANEL;
    }

    @Override
    public boolean withFilteringBehaviour() {
        return true;
    }

    @Override
    public boolean skipOriginalTick() {
        return false;
    }

    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new FactoryPanelScreen(this));
    }

    @Override
    public boolean shouldRenderBulb() {
        return true;
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putString("RecipeAddress", recipeAddress);
        nbt.putInt("RecipeOutput", recipeOutput);
        nbt.putInt("PromiseClearingInterval", promiseClearingInterval);
        nbt.putUUID("Freq", network);
        nbt.put("Craft", NBTHelper.writeItemList(activeCraftingArrangement, registries));
        var asInterface = (FactoryPanelBehaviourAccessor)this;
        nbt.putInt("Timer", asInterface.getTimer());
        nbt.putInt("LastLevel", asInterface.getLastReportedLevelInStorage());
        nbt.putInt("LastPromised", asInterface.getLastReportedPromises());
        nbt.putInt("LastUnloadedLinks", asInterface.getLastReportedUnloadedLinks());
    }

    @Override
    public ItemStack getFilter() {
        return super.getFilter();
    }
}
