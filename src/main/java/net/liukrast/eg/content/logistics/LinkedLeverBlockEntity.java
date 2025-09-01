package net.liukrast.eg.content.logistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.ColoredFactoryPanelSupportBehaviour;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
                this,
                () -> true,
                () -> transmittedSignal > 0,
                () -> {}
        ) {

            @Override
            public Line getColor(FactoryPanelBehaviour be) {
                boolean bool = !(be instanceof AbstractPanelBehaviour ab) || ab.hasConnection(DeployerPanelConnections.REDSTONE.get());
                return new ColoredFactoryPanelSupportBehaviour.Line(bool ? (transmittedSignal > 0 ? 0xEF0000:0x580101) : 0x888898, false);
            }
        });
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
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("Signal", transmittedSignal);
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        transmittedSignal = tag.getInt("Signal");
    }

    @Override
    public void remove() {
        super.remove();
        panelSupport.notifyPanels();
    }
}
