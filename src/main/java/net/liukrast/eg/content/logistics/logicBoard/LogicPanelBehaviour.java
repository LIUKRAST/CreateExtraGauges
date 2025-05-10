package net.liukrast.eg.content.logistics.logicBoard;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class LogicPanelBehaviour extends AbstractPanelBehaviour {

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
        this.label = Component.translatable("create.logistics.logic_gate");
        value = 0;
        max = options.length - 1;
    }

    // Scroll Behaviour
    private final LogicalMode[] options = LogicalMode.class.getEnumConstants();
    public int value;
    public Component label;
    protected int max = 1;
    int min = 0;

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

    public LogicalMode get() {
        return options[value];
    }

    // End Scroll behaviour

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(PanelConnections.REDSTONE, () -> redstonePowered);
    }

    @Override
    public Item getItem() {
        return RegisterItems.LOGIC_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RegisterPartialModels.LOGIC_PANEL;
    }

    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new LogicPanelScreen(this));
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if(!getWorld().isClientSide())
            checkForRedstoneInput();
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active)
            return;
        List<Boolean> powerList = new ArrayList<>();
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            if(linkAt == null) return;
            if(!linkAt.isOutput()) continue;
            powerList.add(linkAt.shouldPanelBePowered());
        }
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelBehaviour at = at(getWorld(), connection);
            if(at == null) return;
            var opt = PanelConnections.getConnectionValue(at, PanelConnections.REDSTONE);
            if(opt.isEmpty()) return;
            powerList.add(opt.get());
        }

        boolean shouldPower = get().test(powerList.stream());
        //End logical mode
        if(shouldPower == redstonePowered)
            return;
        redstonePowered = shouldPower;
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
    public IntAttached<MutableComponent> getDisplayLinkComponent() {
        boolean active = getConnectionValue(PanelConnections.REDSTONE).orElse(false);
        return IntAttached.with(active ? 1 : 0, Component.literal(active ? "✔ True" : "✖ False"));
    }
}
