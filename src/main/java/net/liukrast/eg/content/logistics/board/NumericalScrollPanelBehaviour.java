package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public abstract class NumericalScrollPanelBehaviour extends ScrollPanelBehaviour {
    public NumericalScrollPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.comparator_gate"), type, be, slot);
        withFormatter(String::valueOf);
        between(-256, 256);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(Component.literal("Positive")
                        .withStyle(ChatFormatting.BOLD),
                Component.literal("Negative")
                        .withStyle(ChatFormatting.BOLD));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 256, 32, rows, formatter);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = valueSetting.value();
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(valueSetting.row() == 0 ? -value : value);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(value < 0 ? 0 : 1, Math.abs(value));
    }

    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.number(settings.value())
                .component();
    }

    @Override
    public String getClipboardKey() {
        return "Numerical";
    }
}
