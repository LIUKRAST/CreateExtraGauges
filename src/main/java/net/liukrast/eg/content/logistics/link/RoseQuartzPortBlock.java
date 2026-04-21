package net.liukrast.eg.content.logistics.link;

import com.mojang.serialization.MapCodec;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class RoseQuartzPortBlock extends PortBlock<RoseQuartzPortBlockEntity> {

    public static final MapCodec<RoseQuartzPortBlock> CODEC = simpleCodec(RoseQuartzPortBlock::new);
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public RoseQuartzPortBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWER));
    }

    @Override
    public void updateSignalIn(BlockState state, Level level, BlockPos pos) {
        if(level.isClientSide) return;
        if(!state.getValue(OUTPUT)) return;
        int old = state.getValue(POWER);
        int signal = level.getBestNeighborSignal(pos);
        if(old == signal) return;
        level.setBlock(pos, state.setValue(POWER, signal), 3);
        withBlockEntityDo(level, pos, port -> port.panelSupport.notifyPanels());
    }

    @Override
    public void updateSignalOut(BlockState state, Level level, BlockPos pos) {
        if(level.isClientSide) return;
        if(state.getValue(OUTPUT)) return;
        List<Float> result = getBlockEntityOptional(level, pos)
                .map(be -> be.panelSupport)
                .map(support -> support.getAllValues(DeployerPanelConnections.NUMBERS.get()))
                .orElse(null);
        if(result == null) return;
        int total = result.stream().mapToInt(f -> (int)(float)f).sum();
        int previous = state.getValue(POWER);
        if(total != previous) {
            level.setBlock(pos, state.setValue(POWER, Mth.clamp(total, 0, 15)), 3);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }
        }
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
        return !state.getValue(OUTPUT) ? state.getValue(POWER) : 0;
    }


    @Override
    public Class<RoseQuartzPortBlockEntity> getBlockEntityClass() {
        return RoseQuartzPortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RoseQuartzPortBlockEntity> getBlockEntityType() {
        return EGBlockEntityTypes.ROSE_QUARTZ_PORT.get();
    }
}
