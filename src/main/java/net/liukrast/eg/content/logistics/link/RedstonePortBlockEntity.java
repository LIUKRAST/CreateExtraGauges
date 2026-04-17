package net.liukrast.eg.content.logistics.link;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class RedstonePortBlockEntity extends SmartBlockEntity {

    public FactoryPanelSupportBehaviour panelSupport;

    public RedstonePortBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.REDSTONE_PORT.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        assert level != null;
        behaviours.add(panelSupport = new FactoryPanelSupportBehaviour(this,
                () -> getBlockState().getValue(RedstonePortBlock.OUTPUT),
                () -> getBlockState().getValue(BlockStateProperties.POWERED),
                () -> EGBlocks.REDSTONE_PORT.get().updateSignalOut(getBlockState(), level, worldPosition)
        ));
    }

    @Override
    public void remove() {
        super.remove();
        panelSupport.notifyPanels();
    }
}
