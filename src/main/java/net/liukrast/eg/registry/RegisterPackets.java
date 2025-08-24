package net.liukrast.eg.registry;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.networking.FactoryPanelChangeSizePacket;
import net.liukrast.eg.networking.PanelCacheUpdatePacket;
import net.liukrast.eg.networking.StringPanelUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum RegisterPackets {
    FACTORY_PANEL_CHANGE_SIZE(FactoryPanelChangeSizePacket.class, FactoryPanelChangeSizePacket::new, NetworkDirection.PLAY_TO_SERVER),
    STRING_PANEL_UPDATE(StringPanelUpdatePacket.class, StringPanelUpdatePacket::new, NetworkDirection.PLAY_TO_SERVER),
    PANEL_CACHE_UPDATE(PanelCacheUpdatePacket.class, PanelCacheUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);

    public static final ResourceLocation CHANNEL_NAME = EGConstants.id("main");
    public static final int NETWORK_VERSION = 1;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    private static SimpleChannel channel;

    private final PacketType<?> packetType;

    <T extends SimplePacketBase> RegisterPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packetType = new PacketType<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();
        for (var packet : values()) {
            packet.packetType.register();
        }
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;
        private final NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel().messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }


}
