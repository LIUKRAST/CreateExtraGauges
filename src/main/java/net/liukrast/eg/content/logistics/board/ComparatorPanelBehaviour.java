package net.liukrast.eg.content.logistics.board;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.util.CacheContainer;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ComparatorPanelBehaviour extends NumericalScrollPanelBehaviour implements CacheContainer<Integer> {
    int comparatorMode = 0;
    private int updated = 0;
    private final Map<BlockPos, Integer> cache = new HashMap<>();

    public ComparatorPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.comparator_value"), type, be, slot);
        between(-256, 256);
    }

    @Override
    public Map<BlockPos, Integer> cacheMap() {
        return cache;
    }

    @Override
    public Codec<Integer> cacheCodec() {
        return Codec.INT;
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
        builder.put(EGPanelConnections.REDSTONE, () -> !redstonePowered ? 15 : 0);
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("ComparatorMode", comparatorMode);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        comparatorMode = nbt.getInt("ComparatorMode");
    }

    @Override
    public Item getItem() {
        return EGItems.COMPARATOR_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.COMPARATOR_PANEL;
    }

    @Override
    public void tick() {
        super.tick();
        updated = 0;
    }

    @Override
    public boolean shouldRenderBulb(boolean original) {
        return true;
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active || updated > ExtraGaugesConfig.COMPARATOR_MAX_CHAIN.get())
            return;
        AtomicInteger result = new AtomicInteger();
        consumeForLinks(link -> {
            if(link.shouldPanelBePowered() && link.blockEntity instanceof RedstoneLinkBlockEntity redstoneLink) {
                result.addAndGet(redstoneLink.getReceivedSignal());
            } else result.addAndGet(link.shouldPanelBePowered() ? 1 : 0);
        });
        consumeForPanels(EGPanelConnections.INTEGER.get(), result::addAndGet);
        consumeForExtra(EGPanelConnections.INTEGER.get(), (pos, v) -> {
            result.addAndGet(v);
            cache.put(pos, v);
        });
        sendCache(this);
        boolean shouldPower = ComparatorMode.class.getEnumConstants()[comparatorMode]
                .test(result.get(), value);
        //End logical mode
        if(shouldPower != redstonePowered) return;
        redstonePowered = !shouldPower;
        blockEntity.notifyUpdate();
        updated++;
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
        boolean active = getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
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

    @Override
    public int calculatePath(FactoryPanelBehaviour other, int original) {
        if(EGPanelConnections.getConnectionValue(other, EGPanelConnections.INTEGER).isPresent()) return 0x006496;
        return super.calculatePath(other, original);
    }

    @Override
    public int calculateExtraPath(BlockPos pos) {
        var level = getWorld();
        var state = level.getBlockState(pos);
        var be = level.getBlockEntity(pos);
        var intListener = EGPanelConnections.INTEGER.get().getListener(state.getBlock());
        if(intListener != null) {
            var opt = intListener.invalidate(level, state, pos, be);
            var cache = this.cache.get(pos);
            if(opt.isPresent()) return !ExtraGaugesConfig.PANEL_CACHING.get() || opt.get().equals(cache) ? 0x006496:WAITING;
        }
        var listener = EGPanelConnections.REDSTONE.get().getListener(state.getBlock());
        if(listener == null) return super.calculateExtraPath(pos);
        return listener.invalidate(level, state, pos, be).map(v -> {
            boolean k = v == 0;
            var cache = this.cache.get(pos);
            if(ExtraGaugesConfig.PANEL_CACHING.get() && cache != null && k == cache > 0) return WAITING;
            return k?0x580101:0xEF0000;
        }).orElse(super.calculateExtraPath(pos));
    }

}
