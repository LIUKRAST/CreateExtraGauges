package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.deployer.lib.logistics.board.PanelConfigurationPacket;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.liukrast.eg.registry.EGPackets;
import net.liukrast.eg.registry.EGPanels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class StringPanelUpdatePacket extends PanelConfigurationPacket<StringPanelBehaviour> {
    public static final StreamCodec<RegistryFriendlyByteBuf, StringPanelUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            ByteBufCodecs.STRING_UTF8, packet -> packet.join,
            ByteBufCodecs.STRING_UTF8, packet -> packet.regex,
            ByteBufCodecs.STRING_UTF8, packet -> packet.replace,
            StringPanelUpdatePacket::new
    );
    private final String join,regex,replace;

    public StringPanelUpdatePacket(FactoryPanelPosition position, String join, String regex, String replace) {
        super(position, EGPanels.STRING.get());
        this.join = join;
        this.regex = regex;
        this.replace = replace;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.STRING_PANEL_UPDATE;
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, StringPanelBehaviour panel) {
        panel.setFilter(join, regex, replace);
    }
}
