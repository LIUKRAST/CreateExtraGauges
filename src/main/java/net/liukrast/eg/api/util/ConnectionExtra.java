package net.liukrast.eg.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

@FunctionalInterface
public interface ConnectionExtra<T> {
    Optional<T> invalidate(Level level, BlockState state, BlockPos pos, BlockEntity be);
}
