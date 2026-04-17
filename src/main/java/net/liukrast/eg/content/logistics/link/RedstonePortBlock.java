package net.liukrast.eg.content.logistics.link;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.liukrast.eg.registry.EGBlockEntityTypes;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RedstonePortBlock extends FaceAttachedHorizontalDirectionalBlock implements IBE<RedstonePortBlockEntity>, IWrenchable {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final MapCodec<RedstonePortBlock> CODEC = simpleCodec(RedstonePortBlock::new);

    public RedstonePortBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
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
        super.createBlockStateDefinition(builder.add(FACING, FACE, POWERED, OUTPUT));
    }

    @Override
    public Class<RedstonePortBlockEntity> getBlockEntityClass() {
        return RedstonePortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstonePortBlockEntity> getBlockEntityType() {
        return EGBlockEntityTypes.REDSTONE_PORT.get();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public void updateSignalIn(BlockState state, Level level, BlockPos pos) {
        if(level.isClientSide) return;
        if(!state.getValue(OUTPUT)) return;
        boolean old = state.getValue(POWERED);
        if(old == level.hasNeighborSignal(pos)) return;
        level.setBlock(pos, state.setValue(POWERED, !old), 3);
        withBlockEntityDo(level, pos, port -> port.panelSupport.notifyPanels());
    }

    public void updateSignalOut(BlockState state, Level level, BlockPos pos) {
        if(level.isClientSide) return;
        if(state.getValue(OUTPUT)) return;
        int powerFromPanels = getBlockEntityOptional(level, pos).map(be -> {
                    if (be.panelSupport == null)
                        return 0;
                    Boolean tri = be.panelSupport.shouldBePoweredTristate();
                    if (tri == null)
                        return -1;
                    return tri ? 15 : 0;
                })
                .orElse(0);

        // Suppress update if an input panel exists but is not loaded
        if (powerFromPanels == -1)
            return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != powerFromPanels > 0) {
            level.setBlock(pos, state.cycle(POWERED), 3);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }
        }

    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(POWERED) && !state.getValue(OUTPUT);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if(side.getAxis().isHorizontal() && blockState.getValue(FACE) != AttachFace.WALL)
            return 0;
        if(side == Direction.UP && blockState.getValue(FACE) == AttachFace.CEILING)
            return 0;
        if(side == Direction.DOWN && blockState.getValue(FACE) == AttachFace.FLOOR)
            return 0;

        if(blockState.getValue(FACE) != AttachFace.WALL)
            return getSignal(blockState, blockAccess, pos, side);

        if(side != blockState.getValue(FACING))
            return 0;

        return getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return !state.getValue(OUTPUT) && state.getValue(POWERED) ? 15 : 0;
    }
}
