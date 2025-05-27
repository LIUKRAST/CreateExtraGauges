package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.atomic.AtomicBoolean;

public class CounterPanelBehaviour extends NumericalScrollPanelBehaviour {
    private boolean power;
    public CounterPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.counter_threshold"), type, be, slot);
        between(0, 256);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(Component.literal("Positive")
                        .withStyle(ChatFormatting.BOLD));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 256, 32, rows, formatter);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = valueSetting.value();
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(value);
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("Count", count);
        nbt.putBoolean("Power", power);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        count = nbt.getInt("Count");
        power = nbt.getBoolean("Power");
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.INTEGER, () -> count);
        builder.put(EGPanelConnections.REDSTONE, () -> count >= value ? 15 : 0);
    }

    @Override
    public Item getItem() {
        return EGItems.COUNTER_GAUGE.asItem();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.COUNTER_PANEL;
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        AtomicBoolean shouldPower = new AtomicBoolean(false);
        consumeForLinks(link -> shouldPower.set(shouldPower.get() | link.shouldPanelBePowered()));
        consumeForPanels(EGPanelConnections.REDSTONE.get(), out -> shouldPower.set(shouldPower.get() | out > 0));
        consumeForExtra(EGPanelConnections.REDSTONE.get(), out -> shouldPower.set(shouldPower.get() | out > 0));
        //End logical mode
        if(shouldPower.get() == power)
            return;
        power = shouldPower.get();
        if(shouldPower.get()) {
            if (count >= value) count = 0;
            else count++;
        }
        blockEntity.notifyUpdate();
        notifyRedstoneOutputs();
    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        return Component.literal(shortened ? String.valueOf(count) : count + "/" + value);
    }
}
