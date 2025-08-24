package net.liukrast.eg.content.logistics;

import com.mojang.serialization.DynamicOps;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.logistics.ColoredFactoryPanelSupportBehaviour;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.util.DCFinder;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DisplayCollectorBlockEntity extends DisplayLinkBlockEntity {
    private Component component;
    public DisplayCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.DISPLAY_COLLECTOR.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(factoryPanelSupport = new ColoredFactoryPanelSupportBehaviour(
                (be) -> {
                    boolean bool = !(be instanceof AbstractPanelBehaviour ab) || ab.hasConnection(EGPanelConnections.STRING.get());
                    return new ColoredFactoryPanelSupportBehaviour.Line(bool ? 0xFFFFFFFF : 0x888898, true);
                },
                this,
                () -> true,
                () -> false,
                () -> {}
        ));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if(!tag.contains("text")) return;
        DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
        ComponentSerialization.FLAT_CODEC
                .parse(dynamicops, tag.get("text"))
                .resultOrPartial(EGConstants.LOGGER::error)
                .ifPresent(text -> component = text);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if(component != null) {
            DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
            ComponentSerialization.FLAT_CODEC
                    .encodeStart(dynamicops, component)
                    .resultOrPartial(EGConstants.LOGGER::error)
                    .ifPresent(tag1 -> tag.put("text", tag1));
        }
        if(level == null) return;
        var be = this.level.getBlockEntity(getSourcePosition());
        if(!(be instanceof DCFinder finder)) return;
        var set = finder.extra_gauges$targetingDisplayCollectors();
        var pos = getBlockPos();
        if(set.contains(pos)) return;
        set.add(getBlockPos());
        be.setChanged();
    }

    public Component getComponent() {
        return component == null ? Component.empty() : component;
    }

    public void setComponent(Component component) {
        this.component = component;
        factoryPanelSupport.notifyPanels();
    }

    @Override
    public BlockPos getSourcePosition() {
        return worldPosition.offset(targetOffset);
    }

    @Override
    public BlockPos getTargetPosition() {
        for (FactoryPanelPosition position : factoryPanelSupport.getLinkedPanels())
            return position.pos();
        return worldPosition.relative(getDirection());
    }
}
