package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import io.netty.buffer.ByteBuf;
import net.liukrast.deployer.lib.logistics.board.PanelConfigurationPacket;
import net.liukrast.eg.content.logistics.board.comparator.ComparatorPanelBehaviour;
import net.liukrast.eg.registry.EGPackets;
import net.liukrast.eg.registry.EGPanels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public class ComparatorAdvancedSetupPacket extends PanelConfigurationPacket<ComparatorPanelBehaviour> {

    public static final StreamCodec<ByteBuf, int[]> INT_ARRAY_CODEC = ByteBufCodecs.INT
            .apply(ByteBufCodecs.list())
            .map(
                    list -> list.stream().mapToInt(Integer::intValue).toArray(),
                    array -> Arrays.stream(array).boxed().toList()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ComparatorAdvancedSetupPacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            INT_ARRAY_CODEC, packet -> packet.right,
            INT_ARRAY_CODEC, packet -> packet.left,
            ComparatorAdvancedSetupPacket::new
    );

    private final int[] right;
    private final int[] left;

    public ComparatorAdvancedSetupPacket(FactoryPanelPosition position, int[] right, int[] left) {
        super(position, EGPanels.COMPARATOR.get());
        this.right = right;
        this.left = left;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.COMPARATOR_ADVANCED_SETUP;
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, ComparatorPanelBehaviour panel) {
        panel.right = right;
        panel.left = left;
        panel.blockEntity.setChanged();
        panel.blockEntity.sendData();
        panel.checkForRedstoneInput();
    }
}
