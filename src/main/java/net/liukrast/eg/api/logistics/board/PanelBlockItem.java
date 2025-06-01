package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The class for a custom panel block item.
 * */
public class PanelBlockItem extends LogisticallyLinkedBlockItem {

    private final Supplier<PanelType<?>> type;

    public PanelBlockItem(Supplier<PanelType<?>> type, Properties properties) {
        super(AllBlocks.FACTORY_GAUGE.get(), properties);
        this.type = type;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.isEnchanted();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionresult = this.place(new BlockPlaceContext(context));
        if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
            InteractionResult interactionresult1 = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
        } else {
            return interactionresult;
        }
    }

    protected AbstractPanelBehaviour getNewBehaviourInstance(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        return type.get().create(blockEntity, slot);
    }

    public void applyExtraPlacementData(BlockPlaceContext context, FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot targetedSlot) {
        applyToSlot(blockEntity, targetedSlot);
        var message = getPlacedMessage();
        var player = context.getPlayer();
        if(player == null) return;
        var stack = context.getItemInHand();
        if(!context.getPlayer().isCreative()) {
            stack.shrink(1);
            if(stack.isEmpty())
                player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        }
        if(message != null) player.displayClientMessage(message, true);
    }

    public boolean applyToSlot(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        var oldBehaviour = blockEntity.panels.get(slot);
        if(oldBehaviour == null || !oldBehaviour.isActive()) {
            var newBehaviour = getNewBehaviourInstance(blockEntity, slot);
            newBehaviour.active = true;
            blockEntity.attachBehaviourLate(newBehaviour);
            blockEntity.panels.put(slot, newBehaviour);
            blockEntity.redraw = true;
            blockEntity.lastShape = null;
            blockEntity.notifyUpdate();

            if (blockEntity.activePanels() > 1) {
                //noinspection deprecation
                SoundType soundType = blockEntity.getBlockState().getSoundType();
                //noinspection DataFlowIssue
                blockEntity.getLevel().playSound(null, blockEntity.getBlockPos(), soundType.getPlaceSound(), SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            }
            return true;
        }
        return false;
    }

    /**
     * Whether the itemStack is ready to be placed. For instance, the default Factory Gauge cannot be placed unless it's tuned to a network
     * @return A Component with the error message. If the message is null, the gauge can be placed
     * */
    public Component isReadyForPlacement(ItemStack stack, Level level, BlockPos pos, Player player) {
        return null;
    }

    /**
     * Gets the display message when the link is added. For instance, the default Factory Gauge will send a message when placed
     * */
    @Nullable
    public Component getPlacedMessage() {
        return null;
    }

    // We want to ignore the registration to the map so that the creative tab won't crash
    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {}

    @Override
    public @NotNull String getDescriptionId() {
        return getOrCreateDescriptionId();
    }
}
