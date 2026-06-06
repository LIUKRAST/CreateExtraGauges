package net.liukrast.eg.content.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class LinkedButtonBlock extends LinkedControlBlock {
    public LinkedButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if(level.isClientSide) return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, true), 3);
            level.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            be.transmit(15);
            level.scheduleTick(pos, this, 30);
            return InteractionResult.SUCCESS;
        });
    }



    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        withBlockEntityDo(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            //*Sound*/ level.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.6F);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
            be.transmit(0);
        });

    }
}
