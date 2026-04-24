package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.logistics.LogisticallyLinked;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.RenderFilterSlot;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

public class FilterPanelBehaviour extends AbstractPanelBehaviour implements RenderFilterSlot {
    public FilterItemStack cachedFilter = FilterItemStack.empty();

    public FilterPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerOutput(DeployerPanelConnections.STOCK_CONNECTION, () -> StockConnection.itemStack(getFilter()));
    }

    @Override
    public BulbState getBulbState() {
        return cachedFilter.isEmpty() ? BulbState.RED : BulbState.GREEN;
    }

    @Override
    public void setNetwork(UUID network) {
        this.network = network;
    }

    @Override
    public ItemStack getFilter() {
        return filter.item();
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        ItemStack filter = stack.copy();
        if (!(stack.getItem() instanceof FilterItem) && !stack.isEmpty())
            return false;
        this.cachedFilter = FilterItemStack.of(filter);
        blockEntity.setChanged();
        blockEntity.sendData();
        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        Block.popResource(getWorld(), getPos(), cachedFilter.item().copy());
    }

    @Override
    public ValueSettings getValueSettings() {
        return null;
    }

    public void tick() {
        super.tick();
        if (!this.getWorld().isClientSide()) return;
        if (!this.active) return;
        this.tickOutline();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (getWorld().isClientSide())
            return;
        reloadRecipe();
    }

    public void reloadRecipe() {
        // Check how many items we need
        int needed = 0;
        for(var panelPos : targeting) {
            var panel = at(getWorld(), panelPos);
            if(panel == null) continue;
            var by = panel.targetedBy.get(this.getPanelPosition());
            if(by == null) continue;
            int toBeAdded = by.amount;
            if(!panel.upTo) toBeAdded *= panel.getMaxStackSize();
            needed+=toBeAdded;
        }
        // Check if any items match that amount
        if(cachedFilter.isEmpty()) {
            return;
        }
        InventorySummary summary = getRelevantSummary();
        if(!filter.isEmpty()) {
            int levelInStorage;
            if(blockEntity.isVirtual())
                levelInStorage = 1;
            else {
                levelInStorage = summary.getCountOf(filter.item());
            }

            if(levelInStorage >= needed) return; // We are good
        }

        for(BigItemStack stack : summary.getStacks()) { //If there is lag, we might want to switch to non-by-count logic, so no sort is involved
            if(!cachedFilter.test(getWorld(), stack.stack)) continue;
            if(stack.count < needed) continue;
            for(var panelPos : targeting) {
                var panel = at(getWorld(), panelPos);
                if(panel == null) continue;
                for(int i = 0; i < panel.activeCraftingArrangement.size(); i++) {
                    if(!ItemStack.isSameItemSameComponents(panel.activeCraftingArrangement.get(i), this.filter.item())) continue;
                    panel.activeCraftingArrangement.set(i, stack.stack.copyWithCount(panel.activeCraftingArrangement.get(i).getCount()));
                }
            }
            this.filter = FilterItemStack.of(stack.stack.copy());
            blockEntity.setChanged();
            blockEntity.sendData();
            return;
        }

    }

    @ApiStatus.Internal
    public void ponder$setFilter(ItemStack stack) {
        this.filter = FilterItemStack.of(stack);
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
    public void setValueSettings(Player player, ValueSettings settings, boolean ctrlDown) {
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return null;
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.put("Filter", getFilter().saveOptional(registries));
        nbt.putUUID("Freq", this.network);
        nbt.put("CurrentItemStack", cachedFilter.item().saveOptional(registries));
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        filter = FilterItemStack.of(registries, nbt.getCompound("Filter"));
        cachedFilter = FilterItemStack.of(registries, nbt.getCompound("CurrentItemStack"));
    }

    @Override
    public Item getItem() {
        return EGItems.FILTER_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.FILTER_PANEL;
    }

    private void tickOutline() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> LogisticallyLinkedClientHandler.tickPanel(this));
    }

    public String canConnect(FactoryPanelBehaviour from) {
        return getFilter().isEmpty() || cachedFilter.isEmpty() ? "factory_panel.no_item" : super.canConnect(from);
    }

    @Override
    public void reset() {
        eject();
        this.filter = FilterItemStack.empty();
    }

    public void eject() {
        if (cachedFilter.isFilterItem()) {
            Vec3 pos = VecHelper.getCenterOf(getPos());
            Level world = getWorld();
            world.addFreshEntity(new ItemEntity(world, pos.x, pos.y, pos.z, cachedFilter.item().copy()));
        }
        this.cachedFilter = FilterItemStack.empty();
        blockEntity.setChanged();
        blockEntity.sendData();
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult, boolean client) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (cachedFilter.isEmpty()) {
            if (heldItem.isEmpty()) {
                if (!client && player instanceof ServerPlayer sp) {
                    sp.openMenu(this, (buf) -> FactoryPanelPosition.STREAM_CODEC.encode(buf, this.getPanelPosition()));
                }

            } else {
                Level level = getWorld();
                BlockPos pos = getPos();
                ItemStack itemInHand = player.getItemInHand(hand);
                ItemStack toApply = itemInHand.copy();

                if (level.isClientSide())
                    return;

                if (cachedFilter.item().getItem() instanceof FilterItem) {
                    if (!player.isCreative() || ItemHelper
                            .extract(new InvWrapper(player.getInventory()),
                                    stack -> ItemStack.isSameItemSameComponents(stack, cachedFilter.item()), true)
                            .isEmpty())
                        player.getInventory()
                                .placeItemBackInInventory(cachedFilter.item().copy());
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
        } else if (!(heldItem.getItem() instanceof LogisticallyLinkedBlockItem) && !(heldItem.getItem() instanceof LogisticallyLinked)) {
            super.onShortInteract(player, hand, side, hitResult, client);
        } else if (!client) LogisticallyLinkedBlockItem.assignFrequency(heldItem, player, this.network);
    }

    @Override
    public MutableComponent getLabel() {
        if(cachedFilter.isEmpty())
            return Component.translatable("filter_panel.no_filter")
                    .withStyle(ChatFormatting.RED);
        return Component.translatable("filter_panel.active");
    }

    @Override
    public MutableComponent getCountLabelForValueBox() {
        return Component.empty();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new FilterPanelScreen(this));
    }
}
