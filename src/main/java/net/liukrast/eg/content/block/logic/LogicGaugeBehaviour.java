package net.liukrast.eg.content.block.logic;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.content.EGIcons;
import net.liukrast.eg.content.util.FPSBMExtraMethods;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.function.Function;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour.linkAt;

public class LogicGaugeBehaviour extends ScrollOptionBehaviour<LogicGaugeBehaviour.LogicGate> {

    public Map<BlockPos, FactoryPanelConnection> targetedByLinks;

    public LogicGaugeBehaviour(Component label, SmartBlockEntity be) {
        super(LogicGate.class, label, be, new LogicGaugePanelSlotPositioning());
        this.targetedByLinks = new HashMap<>();
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        if(player.level().isClientSide) CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> displayScreen(player));
        super.onShortInteract(player, hand, side, hitResult);
    }

    @OnlyIn(Dist.CLIENT)
    public void displayScreen(Player player) {
        if(player instanceof LocalPlayer)
            ScreenOpener.open(new LogicGaugeScreen(this));
    }

    public void moveTo(BlockPos newPos, ServerPlayer player) {
        Level level = getWorld();
        BlockState existingState = level.getBlockState(newPos);

        // Check if target pos is valid
        if(!existingState.isAir())
        for(BlockPos blockPos : targetedByLinks.keySet())
            if(!blockPos.closerThan(newPos, 24))
                return;

        // Disconnect links
        for(BlockPos pos : targetedByLinks.keySet()) {
            FactoryPanelSupportBehaviour at = linkAt(level, new FactoryPanelPosition(pos, FactoryPanelBlock.PanelSlot.TOP_LEFT));
            if(at != null)
                ((FPSBMExtraMethods)at).disconnect(this);
        }

        // Relocate be?

        for(BlockPos pos : targetedByLinks.keySet()) {
            FactoryPanelSupportBehaviour at = linkAt(level, new FactoryPanelPosition(pos, FactoryPanelBlock.PanelSlot.TOP_LEFT));
            if(at != null)
                ((FPSBMExtraMethods)at).connect(this);
        }

        // Tell player
        player.displayClientMessage(CreateLang.translate("factory_panel.relocated")
                .style(ChatFormatting.GREEN)
                .component(), true);
        player.level()
                .playSound(null, newPos, SoundEvents.COPPER_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void checkForRedstoneInput() {
        boolean shouldPower = false;
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos()))
                return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            //TODO
        }
    }

    public void notifyRedstoneOutputs() {
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos()))
                return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            if(linkAt == null || linkAt.isOutput())
                return;
            linkAt.notifyLink();
        }
    }

    public FactoryPanelPosition getPanelPosition() {
        return new FactoryPanelPosition(getPos(), FactoryPanelBlock.PanelSlot.TOP_LEFT);
    }

    public void addConnection(FactoryPanelPosition fromPos) {
        FactoryPanelSupportBehaviour link = linkAt(getWorld(), fromPos);
        if(link != null) {
            targetedByLinks.put(fromPos.pos(), new FactoryPanelConnection(fromPos, 1));
            ((FPSBMExtraMethods)link).connect(this);
            blockEntity.notifyUpdate();
        }

        //TODO: Continue logic

    }

    public enum LogicGate implements INamedIconOptions {
        NOT(EGIcons.I_NOT_GATE, stream -> stream.noneMatch(e -> e), false),
        OR(EGIcons.I_OR_GATE, stream -> stream.anyMatch(e -> e), false),
        AND(EGIcons.I_AND_GATE, stream -> stream.allMatch(e -> e), true)
        ;

        private final String translationKey;
        private final AllIcons icon;
        private final boolean nullAction;
        private final Function<Stream<Boolean>, Boolean> function;

        LogicGate(AllIcons icon, Function<Stream<Boolean>, Boolean> function, boolean onNull) {
            this.icon = icon;
            translationKey = "logic_gauge.gate." + Lang.asId(name());
            this.function = function;
            this.nullAction = onNull;
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }

        public boolean nullAction() {
            return nullAction;
        }

        public Boolean test(Stream<Boolean> booleanStream) {
            return function.apply(booleanStream);
        }
    }

    @Override
    public void destroy() {
        disconnectAllLinks();
        super.destroy();
    }

    public void disconnectAllLinks() {
        for (FactoryPanelConnection connection : targetedByLinks.values()) {
            FactoryPanelSupportBehaviour source = linkAt(getWorld(), connection);
            if (source != null)
                ((FPSBMExtraMethods)source).disconnect(this);
        }
        targetedByLinks.clear();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("TargetedByLinks", CatnipCodecUtils.encode(Codec.list(FactoryPanelConnection.CODEC), new ArrayList<>(targetedByLinks.values())).orElseThrow());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        targetedByLinks.clear();
        CatnipCodecUtils.decode(Codec.list(FactoryPanelConnection.CODEC), nbt.get("TargetedByLinks")).orElse(List.of()).forEach(c -> targetedByLinks.put(c.from.pos(), c));
    }
}
