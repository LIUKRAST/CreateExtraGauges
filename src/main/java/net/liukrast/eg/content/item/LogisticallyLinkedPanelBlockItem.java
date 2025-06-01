package net.liukrast.eg.content.item;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.logistics.board.PanelBlockItem;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem.fixCtrlCopiedStack;
import static com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem.assignFrequency;
import static com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem.isTuned;

public class LogisticallyLinkedPanelBlockItem extends PanelBlockItem {
    public LogisticallyLinkedPanelBlockItem(Supplier<PanelType<?>> type, Properties properties) {
        super(type, properties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isTuned(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return super.useOn(pContext);

        LogisticallyLinkedBehaviour link = BlockEntityBehaviour.get(level, pos, LogisticallyLinkedBehaviour.TYPE);
        boolean tuned = isTuned(stack);

        if (link != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!link.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            assignFrequency(stack, player, link.freqId);
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();

        if (!isTuned(stack)) {
            AllSoundEvents.DENY.playOnServer(pContext.getLevel(), pContext.getClickedPos());
            pContext.getPlayer()
                    .displayClientMessage(CreateLang.translate("factory_panel.tune_before_placing")
                            .component(), true);
            return InteractionResult.FAIL;
        }

        return super.place(pContext);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack,
                                                 BlockState state) {
        return super.updateCustomBlockEntityTag(pos, level, player, fixCtrlCopiedStack(stack), state);
    }
}
