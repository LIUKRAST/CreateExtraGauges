package net.liukrast.eg.content.logistics.board;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.util.CacheContainer;
import net.liukrast.eg.content.logistics.IntSelectorBlockEntity;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntPanelBehaviour extends ScrollOptionPanelBehaviour<IntOperationMode> implements CacheContainer<Integer> {
    private int updated = 0;
    private final Map<BlockPos, Integer> cache = new HashMap<>();

    public IntPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.int_operation"), type, be, slot, IntOperationMode.class);
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.INTEGER, () -> count);
        builder.put(EGPanelConnections.REDSTONE, () -> Math.clamp(count, 0, 15));
    }

    @Override
    public Item getItem() {
        return EGItems.INT_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.INT_PANEL;
    }

    /* CACHE */

    @Override
    public Map<BlockPos, Integer> cacheMap() {
        return cache;
    }

    @Override
    public Codec<Integer> cacheCodec() {
        return Codec.INT;
    }

    /* DATA */
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

    /* UPDATE */
    @Override
    public void tick() {
        super.tick();
        updated = 0;
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active || updated > ExtraGaugesConfig.INT_MAX_CHAIN.get())
            return;
        List<Integer> countList = new ArrayList<>();
        consumeForLinks(link -> {
            if(link.shouldPanelBePowered() && link.blockEntity instanceof RedstoneLinkBlockEntity redstoneLink) {
                countList.add(redstoneLink.getReceivedSignal());
            } else if(link.blockEntity instanceof IntSelectorBlockEntity intSelector) {
                countList.add(intSelector.behaviour.getValue());
            } else countList.add(link.shouldPanelBePowered() ? 1 : 0);
        });
        consumeForExtra(EGPanelConnections.INTEGER.get(), (pos, v) -> {
            countList.add(v);
            cache.put(pos, v);
        });
        consumeForPanels(EGPanelConnections.INTEGER.get(), countList::add);

        sendCache(this);
        int result = get().test(countList.stream());

        //End logical mode
        if(result == count)
            return;

        count = result;
        blockEntity.notifyUpdate();
        updated++;
        notifyRedstoneOutputs();
    }

    /* RENDER */
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

    /* DISPLAY LINK */
    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        int n = getConnectionValue(EGPanelConnections.INTEGER).orElse(0);
        String text = shortened ? formatNumber(n) : String.valueOf(n);
        return Component.literal(text);
    }

    private static String formatNumber(int number){
        boolean negative = number < 0;
        number = Math.abs(number);
        if (number >= 1000000) return (negative ? "-":"") + String.format("%.1fM", number / 1000000f);
        if (number >=1000) return (negative ? "-":"") + String.format("%.1fK", number / 1000f);
        return (negative ? "-":"") + number;
    }
}
