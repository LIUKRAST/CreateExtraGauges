package net.liukrast.eg.content.logistics;

import com.simibubi.create.foundation.block.IBE;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

@SuppressWarnings("deprecation")
@NonnullDefault
public class IntSelectorBlock extends FaceAttachedHorizontalDirectionalBlock implements IBE<IntSelectorBlockEntity> {

    public IntSelectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var dir = state.getValue(FACING);
        boolean x1 = dir.getAxis() == Direction.Axis.X;
        boolean x = dir == Direction.EAST;
        boolean z1 = dir.getAxis() == Direction.Axis.Z;
        boolean z = dir == Direction.SOUTH;
        return switch (state.getValue(FACE)) {
            case WALL -> box(
                    x1 ? x ? 0 : 14 : 4,
                    4,
                    z1 ? z ? 0 : 14 : 4,
                    x1 ? x ? 2 : 16 : 12,
                    12,
                    z1 ? z ? 2 : 16 : 12
            );
            case FLOOR -> box(4, 0, 4, 12, 2, 12);
            case CEILING -> box(4, 14, 4, 12, 16, 12);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, FACE));
    }

    @Override
    public Class<IntSelectorBlockEntity> getBlockEntityClass() {
        return IntSelectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends IntSelectorBlockEntity> getBlockEntityType() {
        return EGBlockEntityTypes.INT_SELECTOR.get();
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        return false;
    }
}
