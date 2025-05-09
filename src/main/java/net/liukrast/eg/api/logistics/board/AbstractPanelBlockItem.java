package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The Abstract class for a custom panel block item.
 * */
public abstract class AbstractPanelBlockItem extends BlockItem {

    private final Supplier<PanelType<?>> type;

    public AbstractPanelBlockItem(Supplier<PanelType<?>> type, Properties properties) {
        super(AllBlocks.FACTORY_GAUGE.get(), properties);
        this.type = type;
    }

    /**
     * @return The instance of your new panel behavior
     * */
    protected AbstractPanelBehaviour getNewBehaviourInstance(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        return type.get().create(blockEntity, slot);
    }

    public void applyExtraPlacementData(BlockPlaceContext context, FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot targetedSlot) {
        applyToSlot(blockEntity, targetedSlot);
        var message = getPlacedMessage();
        var player = context.getPlayer();
        if(player == null) return;
        if(message != null) player.displayClientMessage(message, true);
    }

    public boolean applyToSlot(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        var newBehaviour = getNewBehaviourInstance(blockEntity, slot);
        newBehaviour.active = true;
        blockEntity.attachBehaviourLate(newBehaviour);
        blockEntity.panels.put(slot, newBehaviour);
        blockEntity.redraw = true;
        blockEntity.lastShape = null;
        blockEntity.notifyUpdate();
        return true;
    }

    /**
     * Whether the itemStack is ready to be placed. For instance, the default Factory Gauge cannot be placed unless it's tuned to a network
     * */
    public boolean isReadyForPlacement(ItemStack stack, Level level, BlockPos pos, Player player) {
        return true;
    }

    /**
     * Gets the display message when the link is added. For instance, the default Factory Gauge will send a message when placed
     * */
    @Nullable
    public Component getPlacedMessage() {
        return null;
    }

    // We want to ignore the registration to the map, so that creative tab won't crash
    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {}

    @Override
    public @NotNull String getDescriptionId() {
        return getOrCreateDescriptionId();
    }
}
