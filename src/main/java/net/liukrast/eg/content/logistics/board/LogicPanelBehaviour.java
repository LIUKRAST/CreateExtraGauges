package net.liukrast.eg.content.logistics.board;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.*;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.util.CacheContainer;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicPanelBehaviour extends ScrollOptionPanelBehaviour<LogicalMode> implements CacheContainer<Boolean> {
    private int updated = 0;
    private final Map<BlockPos, Boolean> cache = new HashMap<>();

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.logic_gate"), type, be, slot, LogicalMode.class);
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.REDSTONE, () -> !redstonePowered ? 15 : 0);
        builder.put(EGPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
        builder.put(EGPanelConnections.INTEGER.get(), () -> !redstonePowered ? 15 : 0);
    }

    @Override
    public Item getItem() {
        return EGItems.LOGIC_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.LOGIC_PANEL;
    }

    /* CACHE */
    @Override
    public Map<BlockPos, Boolean> cacheMap() {
        return cache;
    }

    @Override
    public Codec<Boolean> cacheCodec() {
        return Codec.BOOL;
    }

    /* DATA */
    /* UPDATE */
    @Override
    public void tick() {
        super.tick();
        updated = 0;
    }

    @Override
    public void checkForRedstoneInput() {
        if(!active || updated > ExtraGaugesConfig.LOGIC_MAX_CHAIN.get())
            return;
        List<Boolean> powerList = new ArrayList<>();
        consumeForLinks(link -> powerList.add(link.shouldPanelBePowered()));
        consumeForExtra(EGPanelConnections.REDSTONE.get(), (pos, out) -> {
            cache.put(pos, out>0);
            powerList.add(out>0);
        });
        consumeForPanels(EGPanelConnections.REDSTONE.get(), out -> powerList.add(out > 0));
        sendCache(this);
        boolean shouldPower = get().test(powerList.stream());
        //End logical mode
        if(shouldPower != redstonePowered)
            return;
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

    /* RENDER */
    @Override
    public int calculatePath(FactoryPanelBehaviour other, int original) {
        return EGPanelConnections.getConnectionValue(other, EGPanelConnections.REDSTONE)
                .map(v -> v == 0 ? 0x580101:0xEF0000)
                .orElse(super.calculatePath(other, original));
    }

    @Override
    public int calculateExtraPath(BlockPos pos) {
        var level = getWorld();
        var state = level.getBlockState(pos);
        var be = level.getBlockEntity(pos);
        var listener = EGPanelConnections.REDSTONE.get().getListener(state.getBlock());
        if(listener == null) return super.calculateExtraPath(pos);
        return listener.invalidate(level, state, pos, be).map(v -> {
            boolean k = v == 0;
            var cache = this.cache.get(pos);
            if(ExtraGaugesConfig.PANEL_CACHING.get() && cache != null && k == cache) return WAITING;
            return k?0x580101:0xEF0000;
        }).orElse(super.calculateExtraPath(pos));
    }

    @Override
    public boolean shouldRenderBulb(boolean original) {
        return true;
    }

    /* DISPLAY LINK */
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
}
