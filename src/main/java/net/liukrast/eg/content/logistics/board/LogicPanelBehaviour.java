package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.IntAttached;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class LogicPanelBehaviour extends ScrollOptionPanelBehaviour<LogicalMode> {
    private boolean power;

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot, LogicalMode.class);

    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(PanelConnections.REDSTONE, () -> power ? 15 : 0);
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putBoolean("Power", power);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        power = nbt.getBoolean("Power");
    }

    @Override
    public Item getItem() {
        return RegisterItems.LOGIC_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RegisterPartialModels.LOGIC_PANEL;
    }


    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        List<Boolean> powerList = new ArrayList<>();
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            if(linkAt == null) return;
            if(!linkAt.isOutput()) continue;
            powerList.add(linkAt.shouldPanelBePowered());
        }
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelBehaviour at = at(getWorld(), connection);
            if(at == null) return;
            var opt = PanelConnections.getConnectionValue(at, PanelConnections.REDSTONE);
            if(opt.isEmpty()) continue;
            powerList.add(opt.get() > 0);
        }

        boolean shouldPower = get().test(powerList.stream());
        //End logical mode
        if(shouldPower == power)
            return;
        power = shouldPower;
        blockEntity.notifyUpdate();
        /*for(FactoryPanelPosition panelPos : targeting) {
            if(!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if(behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        } TODO: Update instantly?

        */
        notifyRedstoneOutputs();
    }

    @Override
    public IntAttached<MutableComponent> getDisplayLinkComponent() {
        boolean active = getConnectionValue(PanelConnections.REDSTONE).orElse(0) > 0;
        return IntAttached.with(active ? 1 : 0, Component.literal(active ? "✔ True" : "✖ False"));
    }
}
