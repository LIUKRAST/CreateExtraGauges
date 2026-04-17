package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.deployer.lib.logistics.board.OrderingPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.logistics.board.connection.StockConnection;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class PassivePanelBehaviour extends OrderingPanelBehaviour {
    public PassivePanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public boolean isFilterEmpty() {
        return getFilter().isEmpty();
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.STOCK_CONNECTION, () -> StockConnection.itemStack(getFilter()));
        super.addConnections(builder);
    }

    @Override
    public Item getItem() {
        return EGItems.PASSIVE_GAUGE.get();
    }

    @Override
    public void tick() {
        int needed = 0;
        if(getWorld().isClientSide()) return;
        for(var panelPos : targeting) {
            var panel = at(getWorld(), panelPos);
            if(panel == null || panel.satisfied || panel.promisedSatisfied) continue;
            var by = panel.targetedBy.get(this.getPanelPosition());
            if(by == null) continue;
            int toBeAdded = (int) (by.amount*Math.ceil((float)panel.count/panel.recipeOutput));
            if(!panel.upTo) toBeAdded *= panel.getMaxStackSize();
            needed+=toBeAdded;
        }
        int stackSize = getMaxStackSize();
        int count;
        boolean upTo;
        if(needed%stackSize == 0) {
            count = needed/stackSize;
            upTo = false;
        } else {
            count = needed;
            upTo = true;
        }
        if(count != this.count || upTo != this.upTo) {
            this.count = count;
            this.upTo = upTo;
            blockEntity.setChanged();
            blockEntity.sendData();
        }
        super.tick();
    }

    @Override
    public void addPromises(RequestPromiseQueue queue) {
        queue.add(new RequestPromise(new BigItemStack(getFilter(), recipeOutput)));
    }

    private InventorySummary getRelevantSummary() {
        FactoryPanelBlockEntity panelBE = panelBE();
        if (!panelBE.restocker)
            return LogisticsManager.getSummaryOfNetwork(network, false);
        PackagerBlockEntity packager = panelBE.getRestockedPackager();
        if (packager == null)
            return InventorySummary.EMPTY;
        return packager.getAvailableItems();
    }

    @Override
    protected int getActualLevelInStorage() {
        InventorySummary summary = getRelevantSummary();
        return summary.getCountOf(getFilter());
    }

    @Override
    public ItemStack getFilter() {
        return filter.item();
    }

    @Override
    protected void forceClearPromises(RequestPromiseQueue queue) {
        queue.forceClear(getFilter());
    }

    @Override
    protected int getTotalPromisedAndRemoveExpired(RequestPromiseQueue queue, int expiry) {
        return queue.getTotalPromisedAndRemoveExpired(getFilter(), expiry);
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.PASSIVE_PANEL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new FactoryPanelScreen(this));
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
    public Component getHoverName() {
        return getFilter().getHoverName()
                .plainCopy();
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.put("Filter", getFilter().saveOptional(registries));
        nbt.putInt("FilterAmount", count);
        nbt.putBoolean("UpTo", upTo);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        filter = FilterItemStack.of(registries, nbt.getCompound("Filter"));
        count = nbt.getInt("FilterAmount");
        upTo = nbt.getBoolean("UpTo");
    }

    @Override
    public void setItem(Player player, InteractionHand hand, Direction side, BlockHitResult blockHitResult, boolean b) {
        Level level = getWorld();
        BlockPos pos = getPos();
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack toApply = itemInHand.copy();

        if (level.isClientSide())
            return;

        if (getFilter(side).getItem() instanceof FilterItem) {
            if (!player.isCreative() || ItemHelper
                    .extract(new InvWrapper(player.getInventory()),
                            stack -> ItemStack.isSameItemSameComponents(stack, getFilter(side)), true)
                    .isEmpty())
                player.getInventory()
                        .placeItemBackInInventory(getFilter(side).copy());
        }

        if (toApply.getItem() instanceof FilterItem)
            toApply.setCount(1);

        if (!setFilter(side, toApply)) {
            player.displayClientMessage(CreateLang.translateDirect("logistics.filter.invalid_item"), true);
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
            return;
        }

        if (!player.isCreative()) {
            if (toApply.getItem() instanceof FilterItem) {
                if (itemInHand.getCount() == 1)
                    player.setItemInHand(hand, ItemStack.EMPTY);
                else
                    itemInHand.shrink(1);
            }
        }

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);
    }

    @Override
    public String getCountName(int i) {
        return i + (upTo ? "" : "▤");
    }

    @Override
    public int getMultiplier() {
        return upTo ? 1 : getMaxStackSize();
    }
}
