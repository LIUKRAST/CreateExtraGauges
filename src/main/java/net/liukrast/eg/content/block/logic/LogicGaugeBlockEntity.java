package net.liukrast.eg.content.block.logic;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.eg.registry.RegisterBlockEntityTypes;
import net.liukrast.eg.registry.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LogicGaugeBlockEntity extends SmartBlockEntity {
    public FactoryPanelSupportBehaviour panelSupport;
    public LogicGaugeBehaviour behaviour;

    public LogicGaugeBlockEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntityTypes.LOGIC_GAUGE.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        Runnable runnable = () -> {
            assert level != null;
            RegisterBlocks.LOGIC_GAUGE.get().updateTransmittedSignal(getBlockState(), level, worldPosition);
        };
        behaviours.add(behaviour = (LogicGaugeBehaviour) new LogicGaugeBehaviour(
                Component.translatable("create.logistics.logic_gate"),
                this
        ).withCallback(i -> runnable.run()));
        behaviours.add(panelSupport =
                new FactoryPanelSupportBehaviour(
                        this,
                        () -> false,
                        () -> false,
                        runnable

                )
        );
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(8);
    }
}
