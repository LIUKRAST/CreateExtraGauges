package net.liukrast.eg.content.logistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.liukrast.eg.api.logistics.ColoredFactoryPanelSupportBehaviour;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class LinkedLeverBlockEntity extends SmartBlockEntity {
    private int transmittedSignal;
    private LinkBehaviour link;

    public FactoryPanelSupportBehaviour panelSupport;

    public LinkedLeverBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.LINKED_LEVER.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(panelSupport = new ColoredFactoryPanelSupportBehaviour(
                (be) -> {
                    boolean bool = !(be instanceof AbstractPanelBehaviour ab) || ab.hasConnection(EGPanelConnections.REDSTONE.get());
                    return new ColoredFactoryPanelSupportBehaviour.Line(bool ? (transmittedSignal > 0 ? 0xEF0000:0x580101) : 0x888898, false);
                },
                this,
                () -> true,
                () -> transmittedSignal > 0,
                () -> {}
        ));
        behaviours.add(link = createLink());
    }

    protected LinkBehaviour createLink() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(LinkedLeverFrequencySlot::new); //TODO: REPLACE WITH FREQUENCY SLOTS POSITION
        return LinkBehaviour.transmitter(this, slots, () -> transmittedSignal);
    }

    public void transmit(int strength) {
        transmittedSignal = strength;
        link.notifySignalChange();
        panelSupport.notifyPanels();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.putInt("Signal", transmittedSignal);
        super.write(tag, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        transmittedSignal = tag.getInt("Signal");
    }

    @Override
    public void remove() {
        super.remove();
        panelSupport.notifyPanels();
    }
}
