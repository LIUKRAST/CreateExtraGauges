package net.liukrast.eg.content.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class LinkedButtonBlock extends LinkedLeverBlock {
    public LinkedButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.isClientSide) return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, true), 3);
            float f = 0.6F;
            level.playSound(player, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
            be.transmit(15);
            level.scheduleTick(pos, this, 30);
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        withBlockEntityDo(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            be.transmit(0);
        });

    }
}
