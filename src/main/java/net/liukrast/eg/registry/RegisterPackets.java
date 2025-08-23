package net.liukrast.eg.registry;

import com.simibubi.create.Create;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.networking.FactoryPanelChangeSizePacket;
import net.liukrast.eg.networking.PanelCacheUpdatePacket;
import net.liukrast.eg.networking.StringPanelUpdatePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum RegisterPackets implements BasePacketPayload.PacketTypeProvider {
    FACTORY_PANEL_CHANGE_SIZE(FactoryPanelChangeSizePacket.class, FactoryPanelChangeSizePacket.STREAM_CODEC),
    STRING_PANEL_UPDATE(StringPanelUpdatePacket.class, StringPanelUpdatePacket.STREAM_CODEC),
    PANEL_CACHE_UPDATE(PanelCacheUpdatePacket.class, PanelCacheUpdatePacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> RegisterPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
                clazz, codec
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(EGConstants.MOD_ID, "1.0.0");
        for (RegisterPackets packet : RegisterPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }


}
