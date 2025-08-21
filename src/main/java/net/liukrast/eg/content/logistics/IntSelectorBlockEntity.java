package net.liukrast.eg.content.logistics;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.logistics.ColoredFactoryPanelSupportBehaviour;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class IntSelectorBlockEntity extends SmartBlockEntity {

    public ScrollValueBehaviour behaviour;
    public ColoredFactoryPanelSupportBehaviour panelSupport;

    public IntSelectorBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.INT_SELECTOR.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        var behaviour = new ScrollValueBehaviour(Component.translatable("create.logistics.int_selection"), this, new IntSelectorValueBox()) {
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
                setValue(valueSetting.row() == 0 ? value : -value);
                panelSupport.notifyPanels();
            }

            @Override
            public ValueSettings getValueSettings() {
                return new ValueSettings(value < 0 ? 1 : 0, Math.abs(value));
            }

            public MutableComponent formatSettings(ValueSettings settings) {
                return CreateLang.number(settings.value())
                        .component();
            }

            @Override
            public String getClipboardKey() {
                return "Numerical";
            }
        };
        behaviour.between(-256, 256);
        behaviours.add(behaviour);
        this.behaviour = behaviour;
        behaviours.add(panelSupport = new ColoredFactoryPanelSupportBehaviour(
                (be) -> new ColoredFactoryPanelSupportBehaviour.Line(0x006496, false),
                this, () -> true, () -> behaviour.value > 0, () -> {}
        ));
    }
}
