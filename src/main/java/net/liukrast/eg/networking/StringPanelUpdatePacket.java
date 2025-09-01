package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.liukrast.eg.registry.EGPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class StringPanelUpdatePacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, StringPanelUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            ByteBufCodecs.STRING_UTF8, packet -> packet.join,
            ByteBufCodecs.STRING_UTF8, packet -> packet.regex,
            ByteBufCodecs.STRING_UTF8, packet -> packet.replace,
            StringPanelUpdatePacket::new
    );
    private final FactoryPanelPosition position;
    private final String join,regex,replace;

    public StringPanelUpdatePacket(FactoryPanelPosition position, String join, String regex, String replace) {
        super(position.pos());
        this.position = position;
        this.join = join;
        this.regex = regex;
        this.replace = replace;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.STRING_PANEL_UPDATE;
    }

    @Override
    protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = be.panels.get(position.slot());
        if(!(behaviour instanceof StringPanelBehaviour stringPanel)) return;
        stringPanel.setFilter(join, regex, replace);
    }
}
