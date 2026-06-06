package net.liukrast.eg.content.logistics.link;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.function.BiConsumer;

@NonnullDefault
public class LinkedButtonBlock extends LinkedControlBlock {
    public LinkedButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        } else {
            return onBlockEntityUse(level, pos, be -> {
                this.use(state, level, pos, player);
                return InteractionResult.SUCCESS;
            });
        }
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks() && !(Boolean)state.getValue(POWERED)) {
            this.use(state, level, pos, null);
        }

        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    protected void use(BlockState state, Level level, BlockPos pos, @Nullable Player player) {
        withBlockEntityDo(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, true), 3);
            level.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            be.transmit(15);
            level.scheduleTick(pos, this, 30);
        });
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        withBlockEntityDo(level, pos, be -> {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            var sound = Math.random() < 0.0001 ? SoundEvents.WARDEN_AGITATED : SoundEvents.STONE_BUTTON_CLICK_OFF;
            level.playSound(null, pos, sound, SoundSource.BLOCKS);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
            be.transmit(0);
        });

    }
}
