package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Function;

/**
 * Allows to register custom connections for the default factory gauge
 * */
public class AddFactoryConnectionEvent extends Event {

    public <T> void addConnection(PanelConnection<T> panelConnection, Function<FactoryPanelBehaviour, T> function) {
        PanelConnections.FACTORY_CONNECTIONS.put(panelConnection, function);
    }

}
