package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.logistics.board.connection.PanelInteractionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringPanelBehaviour extends AbstractPanelBehaviour {
    private static CompiledRegex floatRegex;
    private static CompiledRegex redstoneRegex;

    public String value, join, regex, replacement;
    private float floatValue = 0;
    private CompiledRegex filterRegex;

    public StringPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
        value = join = regex = replacement = "";
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.STRING.get(), this::currentValue);
        builder.registerOutput(DeployerPanelConnections.NUMBERS.get(), () -> floatValue);
        builder.registerOutput(DeployerPanelConnections.REDSTONE.get(), () -> isTruthy(currentValue()));
    }

    private String currentValue() {
        return value == null ? "" : value;
    }

    private static float parseFlexibleFloat(String in) {
        if (in == null || in.isEmpty()) return 0f;
        floatRegex = CompiledRegex.refresh(floatRegex, ExtraGaugesConfig.STRING_TO_FLOAT_REGEX.get());
        if (floatRegex.pattern() == null) return 0f;
        var matcher = floatRegex.pattern().matcher(in);
        if (!matcher.find()) return 0f;
        try {
            return Float.parseFloat(matcher.group());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    public boolean isTruthy(String input) {
        if (input.isEmpty()) return false;
        redstoneRegex = CompiledRegex.refresh(redstoneRegex, ExtraGaugesConfig.STRING_TO_REDSTONE_REGEX.get());
        return redstoneRegex.pattern() != null && redstoneRegex.pattern().matcher(input.toLowerCase()).matches();
    }

    private String applyFilter(String input) {
        if (regex.isEmpty()) return input;
        filterRegex = CompiledRegex.refresh(filterRegex, regex);
        if (filterRegex.pattern() == null) return "RegexError";
        try {
            return filterRegex.pattern().matcher(input).replaceAll(replacement);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return "RegexError";
        }
    }

    private record CompiledRegex(String source, Pattern pattern) {
        private static CompiledRegex refresh(CompiledRegex cached, String source) {
            if (cached != null && cached.source.equals(source)) return cached;
            try {
                return new CompiledRegex(source, Pattern.compile(source));
            } catch (PatternSyntaxException e) {
                return new CompiledRegex(source, null);
            }
        }
    }

    @Override
    public Item getItem() {
        return EGItems.STRING_GAUGE.asItem();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.STRING_PANEL;
    }

    /* DATA */
    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putString("Value", value);
        nbt.putString("Join", join);
        nbt.putString("Regex", regex);
        nbt.putString("Replacement", replacement);
    }

    @Override
    public void easyWriteSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putString("Join", join);
        nbt.putString("Regex", regex);
        nbt.putString("Replacement", replacement);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        value = nbt.getString("Value");
        join = nbt.getString("Join");
        regex = nbt.getString("Regex");
        replacement = nbt.getString("Replacement");
        floatValue = parseFlexibleFloat(value);
    }

    /* UPDATE */

    @Override
    public void notifiedFromInput() {
        if (!active)
            return;
        List<String> result = getAllValues(DeployerPanelConnections.STRING.get());
        if(result == null) return;
        String res = String.join(join, result);
        int maxLength = ExtraGaugesConfig.STRING_MAX_LENGTH.get();
        if (res.length() > maxLength) res = res.substring(0, maxLength);
        res = applyFilter(res);
        if (res.equals(value))
            return;
        value = res;
        floatValue = parseFlexibleFloat(value);

        blockEntity.notifyUpdate();
        notifyOutputs();
    }

    public void setFilter(String join, String regex, String replace) {
        this.join = join;
        this.regex = regex;
        this.replacement = replace;
        blockEntity.notifyUpdate();
        checkForRedstoneInput();
    }

    public String getJoin() {
        return join;
    }

    public String getRegex() {
        return regex;
    }

    public String getReplacement() {
        return replacement;
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
            ScreenOpener.open(new StringPanelScreen(this, hasInteraction("rewriter")));
    }

    @Override
    public void addInteractions(PanelInteractionBuilder builder) {
        builder.registerEntity("rewriter", AllBlockEntityTypes.PACKAGER.get());
    }

    @Override
    public String canConnect(FactoryPanelBehaviour from) {
        if(hasInteraction("rewriter")) return "string_panel.input_in_rewrite_mode";
        return super.canConnect(from);
    }

    @Override
    public void reset() {
        super.reset();
        join = regex = replacement = value = "";
    }
}

