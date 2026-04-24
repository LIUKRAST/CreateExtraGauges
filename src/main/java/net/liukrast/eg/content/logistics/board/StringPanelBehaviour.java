package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringPanelBehaviour extends AbstractPanelBehaviour {
    public String value, join, regex, replacement;
    private float floatValue = 0;

    public StringPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
        value = join = regex = replacement = "";
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
        builder.registerOutput(DeployerPanelConnections.NUMBERS.get(), () -> floatValue);
        builder.registerOutput(DeployerPanelConnections.REDSTONE.get(), () -> isTruthy(getDisplayLinkComponent(false).getString().toLowerCase()));
    }

    private float parseFlexibleFloat(String in) {
        if (in.isEmpty()) return 0f;
        try {
            Pattern pattern = Pattern.compile(ExtraGaugesConfig.STRING_TO_FLOAT_REGEX.get());
            Matcher matcher = pattern.matcher(in);

            if (matcher.find()) {
                try {
                    return Float.parseFloat(matcher.group());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        } catch (PatternSyntaxException ignored) {}
        return 0;
    }

    public boolean isTruthy(String input) {
        if (input.isEmpty()) return false;
        var regex = ExtraGaugesConfig.STRING_TO_REDSTONE_REGEX.get();
        return input.toLowerCase().matches(regex);
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
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putString("Value", value);
        nbt.putString("Join", join);
        nbt.putString("Regex", regex);
        nbt.putString("Replacement", replacement);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
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

        if (!regex.isEmpty()) {
            try {
                Pattern pattern = Pattern.compile(regex);
                res = pattern.matcher(res).replaceAll(replacement);
            } catch (PatternSyntaxException e) {
                res = "RegexError";
            }
        }
        if (res.equals(value))
            return;
        value = res;
        floatValue = parseFlexibleFloat(value);

        blockEntity.notifyUpdate();
        for (FactoryPanelPosition panelPos : targeting) {
            if (!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if (behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
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

