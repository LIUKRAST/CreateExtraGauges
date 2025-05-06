package net.liukrast.eg.content.block.logic;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.liukrast.eg.registry.RegisterBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@NonnullDefault
public class LogicGaugeBlock extends WrenchableDirectionalBlock implements IBE<LogicGaugeBlockEntity> {
    private static final Map<Direction, VoxelShape> SHAPE_PROVIDER = Arrays.stream(Direction.values()).collect(Collectors.toMap(
            key -> key,
            key -> {
                boolean a = key.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                return switch (key.getAxis()) {
                    case X -> Block.box(a ? 0 : 14, 4, 4, a ? 2 : 16, 12, 12);
                    case Y -> Block.box(4, a ? 0 : 14, 4, 12, a ? 2 : 16, 12);
                    case Z -> Block.box(4, 4, a ? 0 : 14, 12, 12, a ? 2 : 16);
                };
            }
    ));
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LogicGaugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public Class<LogicGaugeBlockEntity> getBlockEntityClass() {
        return LogicGaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LogicGaugeBlockEntity> getBlockEntityType() {
        return RegisterBlockEntityTypes.LOGIC_GAUGE.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var facing = state.getValue(FACING);
        return SHAPE_PROVIDER.get(facing);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    public void updateTransmittedSignal(BlockState state, Level worldIn, BlockPos pos) {
        if(worldIn.isClientSide)
            return;
        boolean powerFromPanels = getBlockEntityOptional(worldIn, pos)
                .map(be -> be.panelSupport != null && Boolean.TRUE.equals(be.panelSupport.shouldBePoweredTristate()))
                .orElse(false);
        boolean previouslyPowered = state.getValue(POWERED);
        if(previouslyPowered != powerFromPanels) {
            worldIn.setBlock(pos, state.cycle(POWERED), 2);
            getBlockEntityOptional(worldIn, pos).ifPresent(be -> be.behaviour.notifyRedstoneOutputs());
        }
    }
}
