package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.board.NumericalScrollPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class CounterPanelBehaviour extends NumericalScrollPanelBehaviour {
    public CounterPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.counter_threshold"), type, be, slot);
        between(0, 1024);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerOutput(DeployerPanelConnections.REDSTONE, () -> count >= value);
        builder.registerOutput(DeployerPanelConnections.NUMBERS, () -> (float)count);
        builder.registerInput(DeployerPanelConnections.REDSTONE);
        builder.registerOutput(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
    }

    @Override
    public String formatValue() {
        if(value == 0) return formatter.apply(count);
        return formatter.apply(count) + "/" + formatter.apply(value);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal(""),
                Component.literal(""),
                Component.literal(""),
                Component.literal("")
        );
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(label, 256, 32, rows, formatter);
    }

    @Override
    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.number(settings.row()*256 + settings.value()).component();
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = valueSetting.value();
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(valueSetting.row()*256 + value);
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
        return EGItems.COUNTER_GAUGE.asItem();
    }

    @Override
    public BulbState getBulbState() {
        return redstonePowered ? BulbState.RED : BulbState.DISABLED;
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.COUNTER_PANEL;
    }

    @Override
    public void notifiedFromInput() {
        if(!active)
            return;
        List<Boolean> match = getAllValues(DeployerPanelConnections.REDSTONE.get());
        if(match == null) return;
        boolean shouldPower = match.stream().anyMatch(b -> b);
        if(shouldPower != redstonePowered)
            return;
        redstonePowered = !shouldPower;
        if(shouldPower) {
            if (count >= value && value != 0) count = 0;
            else count++;
        }
        blockEntity.notifyUpdate();
        for(FactoryPanelPosition panelPos : targeting) {
            if(!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if(behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        notifyOutputs();
    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        if(shortened) return Component.literal(String.valueOf(count));
        return Component.literal(count + "/").append(value == 0
                ? Component.translatable("extra_gauges.counter_panel.no_limit")
                : Component.literal(String.valueOf(value)));
    }
}
