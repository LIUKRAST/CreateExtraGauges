package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
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

public class IntPanelBehaviour extends ScrollOptionPanelBehaviour<IntOperationMode> {
    public IntPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.int_operation"), type, be, slot, IntOperationMode.class);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.INTEGER, () -> count);
        builder.put(EGPanelConnections.REDSTONE, () -> Math.clamp(count, 0, 15));
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("Count", count);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        count = nbt.getInt("Count");
    }

    @Override
    public Item getItem() {
        return EGItems.INT_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.INT_PANEL;
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        List<Integer> countList = new ArrayList<>();
        consumeForLinks(link -> {
            if(link.shouldPanelBePowered() && link.blockEntity instanceof RedstoneLinkBlockEntity redstoneLink) {
                countList.add(redstoneLink.getReceivedSignal());
            } else countList.add(link.shouldPanelBePowered() ? 1 : 0);
        });
        consumeForExtra(EGPanelConnections.INTEGER.get(), countList::add);
        consumeForPanels(EGPanelConnections.INTEGER.get(), countList::add);

        int result = get().test(countList.stream());

        //End logical mode
        if(result == count)
            return;

        count = result;
        blockEntity.notifyUpdate();
        notifyRedstoneOutputs();
    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        int n = getConnectionValue(EGPanelConnections.INTEGER).orElse(0);
        String text = shortened ? formatNumber(n) : String.valueOf(n);
        return Component.literal(text);
    }

    private static String formatNumber(int number){
        boolean negative = number < 0;
        number = Math.abs(number);
        if (number >= 1000000) return (negative ? "-":"") + String.format("%.1fM", number / 1000000f);
        if (number >=1000) return (negative ? "-":"") + String.format("%.1fK", number / 1000f);
        return (negative ? "-":"") + number;
    }
}
