package net.liukrast.eg.content.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class LinkedLeverBlock extends LinkedControlBlock {
    public LinkedLeverBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if(level.isClientSide) return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, be -> {
            var cycled = state.cycle(POWERED);
            level.setBlock(pos, cycled, 3);
            boolean isPowered = cycled.getValue(POWERED);
            float f = isPowered ? 0.6F : 0.5F;
            level.playSound(player, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
            level.gameEvent(player, isPowered ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
            be.transmit(getPower(cycled));
            return InteractionResult.SUCCESS;
        });
    }

}
