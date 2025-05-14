package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ScrollPanelBehaviour<E extends Enum<E> & INamedIconOptions> extends AbstractPanelBehaviour {
    private final E[] options;
    public int value;
    public Component label;
    protected int max;
    int min = 0;

    public ScrollPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot,Class<E> enum_) {
        super(type, be, slot);
        this.label = Component.translatable("create.logistics.logic_gate");
        value = 0;
        options = enum_.getEnumConstants();
        max = options.length - 1;
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("ScrollValue", value);
        super.easyWrite(nbt, registries, clientPacket);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        value = nbt.contains("ScrollValue") ? nbt.getInt("ScrollValue") : 0;
        super.easyRead(nbt, registries, clientPacket);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(label, max, 1, ImmutableList.of(Component.literal("Select")),
                new ValueSettingsFormatter.ScrollOptionSettingsFormatter(options));
    }

    @Override
    public String getClipboardKey() {
        return options[0].getClass().getSimpleName();
    }

    public void setValue(int value) {
        value = Mth.clamp(value, min, max);
        if (value == this.value)
            return;
        this.value = value;
        //TODO: Necessary? callback.accept(value);
        checkForRedstoneInput();
        blockEntity.setChanged();
        blockEntity.sendData();
    }

    @Override
    public void setValueSettings(Player player, ValueSettings settings, boolean ctrlDown) {
        if (settings.equals(getValueSettings()))
            return;
        setValue(settings.value());
        playFeedbackSound(this);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, value);
    }

    INamedIconOptions getIconForSelected() {
        return get();
    }

    public E get() {
        return options[value];
    }
}
