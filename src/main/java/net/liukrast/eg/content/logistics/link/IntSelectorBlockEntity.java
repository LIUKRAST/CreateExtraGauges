package net.liukrast.eg.content.logistics.link;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.deployer.lib.logistics.board.connection.AbstractPanelSupportBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class IntSelectorBlockEntity extends SmartBlockEntity {

    public ScrollValueBehaviour behaviour;
    public AbstractPanelSupportBehaviour panelSupport;

    public IntSelectorBlockEntity(BlockPos pos, BlockState state) {
        super(EGBlockEntityTypes.INT_SELECTOR.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        var behaviour = new ScrollValueBehaviour(Component.translatable("create.logistics.int_selection"), this, new IntSelectorValueBox()) {
            @Override
            public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
                ImmutableList<Component> rows = ImmutableList.of(
                        Component.literal("+").withStyle(ChatFormatting.BOLD),
                        Component.literal("+").withStyle(ChatFormatting.BOLD),
                        Component.literal("+").withStyle(ChatFormatting.BOLD),
                        Component.literal("+").withStyle(ChatFormatting.BOLD),
                        Component.literal("-").withStyle(ChatFormatting.BOLD),
                        Component.literal("-").withStyle(ChatFormatting.BOLD),
                        Component.literal("-").withStyle(ChatFormatting.BOLD),
                        Component.literal("-").withStyle(ChatFormatting.BOLD)
                );
                ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
                return new ValueSettingsBoard(label, 256, 32, rows, formatter);
            }

            @Override
            public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
                if (!valueSetting.equals(getValueSettings())) playFeedbackSound(this);
                setValue(formatValue(valueSetting.row(), valueSetting.value()));
                panelSupport.notifyPanels();
            }

            public int formatValue(int row, int val) {
                return row <= 3 ? ((3-row)*256) + val : -((row-4) + val);
            }

            @Override
            public ValueSettings getValueSettings() {
                int row, val;
                if (value >= 0) {
                    int totalFromBottom = value;
                    int localRow = Mth.floor(totalFromBottom / 256f);
                    row = 3 - localRow;
                    val = 255 - (totalFromBottom % 256);
                } else {
                    int absVal = Math.abs(value);
                    int localRow = Mth.floor(absVal / 256f);
                    row = localRow + 4;
                    val = absVal % 256;
                }
                return new ValueSettings(row, val);
            }

            public MutableComponent formatSettings(ValueSettings settings) {
                return CreateLang.number(formatValue(settings.row(), settings.value())).component();
            }

            @Override
            public String getClipboardKey() {
                return "Numerical";
            }
        };
        behaviour.between(-1024, 1024);
        behaviours.add(behaviour);
        this.behaviour = behaviour;
        behaviours.add(panelSupport = new AbstractPanelSupportBehaviour(this, () -> true, () -> {}) {
            @Override
            public void addConnections(PanelConnectionBuilder builder) {
                builder.registerOutput(DeployerPanelConnections.NUMBERS.get(), () -> (float)behaviour.value);
            }
        });
    }
}
