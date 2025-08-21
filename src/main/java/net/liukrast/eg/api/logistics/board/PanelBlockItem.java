package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import java.util.UUID;
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
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        var player = context.getPlayer();
        Component error = isReadyForPlacement(context.getItemInHand(), context.getLevel(), context.getClickedPos(), player);
        if(error == null) return super.place(context);
        AllSoundEvents.DENY.playOnServer(context.getLevel(), context.getClickedPos());
        if(player != null) player.displayClientMessage(error, true);
        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        var player = context.getPlayer();
        if (!interactionResult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD) && player != null) {
            InteractionResult result = super.use(context.getLevel(), player, context.getHand()).getResult();
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        } else {
            return interactionResult;
        }
    }

    protected AbstractPanelBehaviour getNewBehaviourInstance(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        return type.get().create(blockEntity, slot);
    }

    public void applyExtraPlacementData(BlockPlaceContext context, FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot targetedSlot) {
        var stack = context.getItemInHand();
        applyToSlot(blockEntity, targetedSlot, LogisticallyLinkedBlockItem.networkFromStack(FactoryPanelBlockItem.fixCtrlCopiedStack(stack)));
        var message = getPlacedMessage();
        var player = context.getPlayer();
        if(player == null) return;
        if(!context.getPlayer().isCreative()) {
            stack.shrink(1);
            if(stack.isEmpty())
                player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        }
        player.displayClientMessage(message, true);
    }

    public boolean applyToSlot(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot, @Nullable UUID networkId) {
        var oldBehaviour = blockEntity.panels.get(slot);
        if(oldBehaviour == null || !oldBehaviour.isActive()) {
            var newBehaviour = getNewBehaviourInstance(blockEntity, slot);
            newBehaviour.active = true;
            if(networkId != null) newBehaviour.setNetwork(networkId);
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
    public Component isReadyForPlacement(ItemStack stack, Level level, BlockPos pos, @Nullable Player player) {
        return null;
    }

    /**
     * Gets the display message when the link is added. For instance, the default Factory Gauge will send a message when placed
     * */
    @NotNull
    public Component getPlacedMessage() {
        return Component.empty();
    }

    // We want to ignore the registration to the map so that the creative tab won't crash
    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {}

    @Override
    public @NotNull String getDescriptionId() {
        return getOrCreateDescriptionId();
    }
}
