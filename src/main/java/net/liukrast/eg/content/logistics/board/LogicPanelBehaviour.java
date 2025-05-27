package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class LogicPanelBehaviour extends ScrollOptionPanelBehaviour<LogicalMode> {
    public boolean power;

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.logic_gate"), type, be, slot, LogicalMode.class);

    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.REDSTONE, () -> power ? 15 : 0);
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
        return EGItems.LOGIC_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.LOGIC_PANEL;
    }


    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        List<Boolean> powerList = new ArrayList<>();
        consumeForLinks(link -> powerList.add(link.shouldPanelBePowered()));
        consumeForExtra(EGPanelConnections.REDSTONE.get(), out -> powerList.add(out > 0));
        consumeForPanels(EGPanelConnections.REDSTONE.get(), out -> powerList.add(out > 0));

        boolean shouldPower = get().test(powerList.stream());
        //End logical mode
        if(shouldPower == power)
            return;
        power = shouldPower;
        blockEntity.notifyUpdate();
        notifyRedstoneOutputs();
    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        boolean active = getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
        String t = "✔";
        String f = "✖";
        if(!shortened) {
            t += " True";
            f += " False";
        }
        return Component.literal(active ? t : f);
    }
}
