package net.liukrast.eg.registry;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.function.Function;

/**
 * Allows to register custom connections for the default factory gauge
 * */
public class AddFactoryConnectionEvent extends Event {

    public <T> void addConnection(ResourceLocation id, Function<FactoryPanelBehaviour, T> function) {
        EGPanelConnections.FACTORY_CONNECTIONS.put(id, function);
    }

}
