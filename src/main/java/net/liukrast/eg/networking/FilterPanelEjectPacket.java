package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.deployer.lib.logistics.board.PanelConfigurationPacket;
import net.liukrast.eg.content.logistics.board.FilterPanelBehaviour;
import net.liukrast.eg.registry.EGPackets;
import net.liukrast.eg.registry.EGPanels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class FilterPanelEjectPacket extends PanelConfigurationPacket<FilterPanelBehaviour> {
    public static final StreamCodec<RegistryFriendlyByteBuf, FilterPanelEjectPacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            FilterPanelEjectPacket::new
    );

    public FilterPanelEjectPacket(FactoryPanelPosition position) {
        super(position, EGPanels.FILTER.get());
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, FilterPanelBehaviour panel) {
        panel.eject();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.FILTER_PANEL_UPDATE;
    }
}
