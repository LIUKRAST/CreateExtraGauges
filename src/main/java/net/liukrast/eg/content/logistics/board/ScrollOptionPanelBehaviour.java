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

public abstract class ScrollOptionPanelBehaviour<E extends Enum<E> & INamedIconOptions> extends ScrollPanelBehaviour {
    private final E[] options;

    public ScrollOptionPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot, Class<E> enum_) {
        super(Component.translatable("create.logistics.logic_gate"), type, be, slot);
        options = enum_.getEnumConstants();
        between(0, options.length - 1);
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

    INamedIconOptions getIconForSelected() {
        return get();
    }

    public E get() {
        return options[value];
    }
}
