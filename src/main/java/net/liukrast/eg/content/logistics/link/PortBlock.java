package net.liukrast.eg.content.logistics.link;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public abstract class PortBlock<T extends BlockEntity> extends FaceAttachedHorizontalDirectionalBlock implements IBE<T>, IWrenchable {

    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");

    protected PortBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
        if (!isMoving && !state.is(newState.getBlock())) {
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        updateSignalIn(state, level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var dir = state.getValue(FACING);
        boolean x1 = dir.getAxis() == Direction.Axis.X;
        boolean x = dir == Direction.EAST;
        boolean z1 = dir.getAxis() == Direction.Axis.Z;
        boolean z = dir == Direction.SOUTH;
        return switch (state.getValue(FACE)) {
            case WALL -> box(
                    x1 ? x ? 0 : 13 : 3,
                    3,
                    z1 ? z ? 0 : 13 : 3,
                    x1 ? x ? 3 : 16 : 13,
                    13,
                    z1 ? z ? 3 : 16 : 13
            );
            case FLOOR -> box(3, 0, 3, 13, 3, 13);
            case CEILING -> box(3, 13, 3, 13, 16, 13);
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            var newState = state.cycle(OUTPUT);
            level.setBlock(pos, newState, 3);
            updateSignalIn(newState, level, pos);
            updateSignalOut(newState, level, pos);
            IWrenchable.playRotateSound(level, pos);

            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        updateSignalIn(state, level, pos);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        var level = context.getLevel();
        var newState = state.cycle(OUTPUT);
        level.setBlock(context.getClickedPos(), newState, 3);
        updateSignalIn(newState, level, context.getClickedPos());
        updateSignalOut(newState, level, context.getClickedPos());
        IWrenchable.playRotateSound(level, context.getClickedPos());

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, FACE, OUTPUT));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public abstract void updateSignalIn(BlockState state, Level level, BlockPos pos);
    public abstract void updateSignalOut(BlockState state, Level level, BlockPos pos);

    @Override
    public boolean isSignalSource(BlockState state) {
        return !state.getValue(OUTPUT);
    }
}
