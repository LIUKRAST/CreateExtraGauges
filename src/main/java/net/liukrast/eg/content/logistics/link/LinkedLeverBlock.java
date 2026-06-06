package net.liukrast.eg.content.logistics.link;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class LinkedLeverBlock extends LinkedControlBlock {
    public LinkedLeverBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void use(BlockState state, Level level, BlockPos pos, @Nullable Player player) {
        withBlockEntityDo(level, pos, be -> {
            var cycled = state.cycle(POWERED);
            level.setBlock(pos, cycled, 3);
            float f = cycled.getValue(POWERED) ? 0.6F : 0.5F;
            level.playSound(player, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
            level.gameEvent(player, cycled.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
            be.transmit(getPower(cycled));
        });
    }
}
