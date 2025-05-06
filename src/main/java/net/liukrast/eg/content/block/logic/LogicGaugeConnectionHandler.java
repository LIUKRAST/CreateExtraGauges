package net.liukrast.eg.content.block.logic;

import static net.liukrast.eg.mixin.FactoryPanelConnectionHandlerAccessor.*;

public class LogicGaugeConnectionHandler {

    public static void startConnection(LogicGaugeBehaviour behaviour) {
        setRelocating(false);
        setConnectingFrom(behaviour.getPanelPosition());
        var level = behaviour.blockEntity.getLevel();
        var pos = behaviour.blockEntity.getBlockPos();
        setConnectingFromBox(behaviour.blockEntity.getBlockState().getShape(level, pos).bounds());
    }
}
