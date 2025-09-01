package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.liukrast.eg.mixinExtension.WidthModifier;
import net.liukrast.eg.registry.EGPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class FactoryPanelChangeSizePacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, FactoryPanelChangeSizePacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            ByteBufCodecs.INT, packet -> packet.width,
            FactoryPanelChangeSizePacket::new
    );
    private final FactoryPanelPosition position;
    private final int width;

    public FactoryPanelChangeSizePacket(FactoryPanelPosition position, int width) {
        super(position.pos());
        this.position = position;
        this.width = width;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.FACTORY_PANEL_CHANGE_SIZE;
    }

    @Override
    protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = be.panels.get(position.slot());
        if (behaviour == null)
            return;
        WidthModifier extra = (WidthModifier) behaviour;
        if(width > 2) extra.extra_gauges$setWidth(width);
        be.notifyUpdate();
    }
}
