package net.liukrast.eg.content.logistics.link;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.AbstractPanelSupportBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RoseQuartzPortBlockEntity extends SmartBlockEntity {
    public AbstractPanelSupportBehaviour panelSupport;

    public RoseQuartzPortBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.ROSE_QUARTZ_PORT.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        assert level != null;
        behaviours.add(panelSupport = new AbstractPanelSupportBehaviour(this,
                () -> getBlockState().getValue(RedstonePortBlock.OUTPUT),
                () -> EGBlocks.ROSE_QUARTZ_PORT.get().updateSignalOut(getBlockState(), level, worldPosition)
        ) {
            @Override
            public void addConnections(PanelConnectionBuilder builder) {
                builder.registerBoth(DeployerPanelConnections.NUMBERS.get(), () -> (float)getBlockState().getValue(RoseQuartzPortBlock.POWER));
            }
        });
    }
}
