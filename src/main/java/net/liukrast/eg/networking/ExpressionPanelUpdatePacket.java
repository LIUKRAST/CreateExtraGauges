package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.deployer.lib.logistics.board.PanelConfigurationPacket;
import net.liukrast.eg.content.logistics.board.ExpressionPanelBehaviour;
import net.liukrast.eg.registry.EGPackets;
import net.liukrast.eg.registry.EGPanels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ExpressionPanelUpdatePacket extends PanelConfigurationPacket<ExpressionPanelBehaviour> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ExpressionPanelUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            ByteBufCodecs.STRING_UTF8, packet -> packet.expression,
            ExpressionPanelUpdatePacket::new
    );

    private final String expression;

    public ExpressionPanelUpdatePacket(FactoryPanelPosition position, String expression) {
        super(position, EGPanels.EXPRESSION.get());
        this.expression = expression;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return EGPackets.EXPRESSION_PANEL_UPDATE;
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, ExpressionPanelBehaviour panel) {
        panel.setFilter(expression);
    }
}
