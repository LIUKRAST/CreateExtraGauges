package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.mixin.FactoryPanelBehaviourAccessor;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

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
    public void tick() {
        int count = 0;
        for(var panelPos : targeting) {
            var panel = at(getWorld(), panelPos);
            if(panel == null || panel.satisfied || panel.promisedSatisfied) continue;
            var by = panel.targetedBy.get(this.getPanelPosition());
            if(by == null) continue;
            count+=by.amount;
        }
        this.count = count;
        super.tick();
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
    public boolean shouldRenderBulb(boolean original) {
        return original;
    }

    @Override
    public int calculatePath(FactoryPanelBehaviour other, int original) {
        return other instanceof AbstractPanelBehaviour ? EGPanelConnections.getConnectionValue(other, EGPanelConnections.REDSTONE)
                .map(v -> v == 0 ? 0x580101:0xEF0000)
                .orElse(original) : original;
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
    public ValueSettings getValueSettings() {
        return null;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings settings, boolean ctrlDown) {}

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return null;
    }

    @Override
    public MutableComponent getLabel() {
        String key;

        if (targetedBy.isEmpty())
            return Component.translatable("extra_gauges.gui.passive_panel.no_recipe")
                    .withStyle(ChatFormatting.RED);

        if (isMissingAddress())
            return CreateLang.translate("gui.factory_panel.address_missing")
                    .style(ChatFormatting.RED)
                    .component();

        if (getFilter().isEmpty())
            key = "factory_panel.new_factory_task";
        else if (waitingForNetwork)
            key = "factory_panel.some_links_unloaded";
        else if (getAmount() == 0 || targetedBy.isEmpty())
            return getFilter().getHoverName()
                    .plainCopy();
        else {
            key = getFilter().getHoverName()
                    .getString();
            if (redstonePowered)
                key += " " + CreateLang.translate("factory_panel.redstone_paused")
                        .string();
            else if (!satisfied)
                key += " " + CreateLang.translate("factory_panel.in_progress")
                        .string();
            return CreateLang.text(key)
                    .component();
        }

        return CreateLang.translate(key)
                .component();
    }

    @Override
    public boolean ignoreIssue(@Nullable String issue) {
        return false;
    }
}
