package net.liukrast.eg.content.logistics.board;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.api.util.CacheContainer;
import net.liukrast.eg.content.logistics.DisplayCollectorBlockEntity;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPanelConnections;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringPanelBehaviour extends AbstractPanelBehaviour implements CacheContainer<String> {
    @Nullable
    private String value, join, regex, replacement;
    private int intValue = 0;
    private int updated = 0;
    private final Map<BlockPos, String> cache = new HashMap<>();

    public StringPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.put(EGPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
        builder.put(EGPanelConnections.INTEGER.get(), () -> intValue);
        builder.put(EGPanelConnections.REDSTONE.get(), () -> Mth.clamp(intValue, 0, 15));
    }

    @Override
    public Item getItem() {
        return EGItems.STRING_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.STRING_PANEL;
    }

    /* CACHE */
    @Override
    public Map<BlockPos, String> cacheMap() {
        return cache;
    }

    @Override
    public Codec<String> cacheCodec() {
        return Codec.STRING;
    }

    /* DATA */
    @Override
    public void easyWrite(CompoundTag nbt, boolean clientPacket) {
        super.easyWrite(nbt, clientPacket);
        if (value != null && !value.isEmpty()) nbt.putString("Value", value);
        if (join != null && !join.isEmpty()) nbt.putString("Join", join);
        if (regex != null && !regex.isEmpty()) nbt.putString("Regex", regex);
        if (replacement != null && !replacement.isEmpty()) nbt.putString("Replacement", replacement);
    }

    @Override
    public void easyRead(CompoundTag nbt, boolean clientPacket) {
        super.easyRead(nbt, clientPacket);
        value = nbt.contains("Value") ? nbt.getString("Value") : null;
        join = nbt.contains("Join") ? nbt.getString("Join") : null;
        regex = nbt.contains("Regex") ? nbt.getString("Regex") : null;
        replacement = nbt.contains("Replacement") ? nbt.getString("Replacement") : null;
        if (value != null) {
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                intValue = 0;
            }
        }
    }

    /* UPDATE */
    @Override
    public void tick() {
        super.tick();
        updated = 0;
    }

    @Override
    public void checkForRedstoneInput() {
        if (!active || updated > ExtraGaugesConfig.STRING_MAX_CHAIN.get())
            return;
        StringJoiner stringList = new StringJoiner(join == null ? "" : join);
        consumeForLinks(link -> {
            if (link.blockEntity instanceof DisplayCollectorBlockEntity collector)
                stringList.add(collector.getComponent().getString());
        });
        consumeForExtra(EGPanelConnections.STRING.get(), (pos, v) -> {
            cache.put(pos, v);
            stringList.add(v);
        });
        consumeForPanels(EGPanelConnections.STRING.get(), stringList::add);
        String result = stringList.toString();
        int maxLength = ExtraGaugesConfig.STRING_MAX_LENGTH.get();
        if (result.length() > maxLength) result = result.substring(0, maxLength);

        if (regex != null && !regex.isEmpty()) {
            try {
                Pattern pattern = Pattern.compile(regex);
                result = pattern.matcher(result).replaceAll(replacement == null ? "" : replacement);
            } catch (PatternSyntaxException e) {
                result = "RegexError";
            }
        }
        sendCache(this);
        if (result.equals(value))
            return;
        value = result;
        try {
            intValue = Math.round(Float.parseFloat(value));
        } catch (NumberFormatException ignored) {
            intValue = 0;
        }

        blockEntity.notifyUpdate();
        updated++;
        for (FactoryPanelPosition panelPos : targeting) {
            if (!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if (behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        notifyRedstoneOutputs();
    }

    public void setFilter(String join, String regex, String replace) {
        this.join = join;
        this.regex = regex;
        this.replacement = replace;
        blockEntity.notifyUpdate();
        checkForRedstoneInput();
    }

    public @Nullable String getJoin() {
        return join;
    }

    public @Nullable String getRegex() {
        return regex;
    }

    public @Nullable String getReplacement() {
        return replacement;
    }

    /* RENDER */
    @Override
    public int calculatePath(FactoryPanelBehaviour other, int original) {
        return EGPanelConnections.getConnectionValue(other, EGPanelConnections.STRING).map(str -> 0xFFFFFF).orElse(super.calculatePath(other, original));
    }

    @Override
    public int calculateExtraPath(BlockPos pos) {
        var level = getWorld();
        var state = level.getBlockState(pos);
        var be = level.getBlockEntity(pos);
        var listener = EGPanelConnections.STRING.get().getListener(state.getBlock());
        if (listener != null) {
            var opt = listener.invalidate(level, state, pos, be);
            var cache = this.cache.get(pos);
            if (opt.isPresent())
                return !ExtraGaugesConfig.PANEL_CACHING.get() || cache != null && cache.equals(opt.get()) ? 0xFFFFFF : WAITING;
        }
        return super.calculateExtraPath(pos);
    }

    /* DISPLAY LINK */
    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortenNumbers) {
        return value == null ? Component.empty() : Component.literal(value);
    }

    /* SCREEN */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new StringPanelScreen(this));
    }
}

