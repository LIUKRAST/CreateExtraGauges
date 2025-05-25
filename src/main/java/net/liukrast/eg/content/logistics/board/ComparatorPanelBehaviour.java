package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ComparatorPanelBehaviour extends NumericalScrollPanelBehaviour {
    int comparatorMode = 0;
    public boolean power = false;

    public ComparatorPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
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
        if(valueSetting.row() == 2) {
            comparatorMode = Mth.clamp(valueSetting.value(), 0, ComparatorMode.values().length-1);
            checkForRedstoneInput();
        } else {
            int value = valueSetting.value();
            if (!valueSetting.equals(getValueSettings()))
                playFeedbackSound(this);
            setValue(valueSetting.row() == 0 ? value : -value);
        }
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(PanelConnections.REDSTONE, () -> power ? 15 : 0);
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("ComparatorMode", comparatorMode);
        nbt.putBoolean("Power", power);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        comparatorMode = nbt.getInt("ComparatorMode");
        power = nbt.getBoolean("Power");
    }

    @Override
    public Item getItem() {
        return RegisterItems.COMPARATOR_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RegisterPartialModels.COMPARATOR_PANEL;
    }

    @Override
    public ValueSettings getValueSettings() {
        return super.getValueSettings();
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        int result = 0;
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            if(linkAt == null) return;
            if(!linkAt.isOutput()) continue;
            //TODO: add better compatibility with other mods
            if(linkAt.shouldPanelBePowered() && linkAt.blockEntity instanceof RedstoneLinkBlockEntity redstoneLink) {
                result += redstoneLink.getReceivedSignal();
            } else result += linkAt.shouldPanelBePowered() ? 1 : 0;
        }
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelBehaviour at = at(getWorld(), connection);
            if(at == null) return;
            var opt = PanelConnections.getConnectionValue(at, PanelConnections.INTEGER);
            if(opt.isEmpty()) continue;
            result += opt.get();
        }

        boolean shouldPower = ComparatorMode.class.getEnumConstants()[comparatorMode]
                .test(result, value);
        //End logical mode
        if(shouldPower == power)
            return;
        power = shouldPower;
        blockEntity.notifyUpdate();
        for(FactoryPanelPosition panelPos : targeting) {
            if(!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if(behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        notifyRedstoneOutputs();

    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        boolean active = getConnectionValue(PanelConnections.REDSTONE).orElse(0) > 0;
        String t = "✔";
        String f = "✖";
        if(!shortened) {
            t += " True";
            f += " False";
        }
        return Component.literal(active ? t : f);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new ComparatorPanelScreen(this));
    }
}
