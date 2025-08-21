package net.liukrast.eg.registry;

import com.simibubi.create.AllDisplaySources;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EGPanelConnections {
    private EGPanelConnections() {}
    static final Map<PanelConnection<?>, Function<FactoryPanelBehaviour, ?>> FACTORY_CONNECTIONS = new Reference2ObjectArrayMap<>();
    private static final DeferredRegister<PanelConnection<?>> CONNECTIONS = DeferredRegister.create(EGRegistries.PANEL_CONNECTION_REGISTRY, ExtraGauges.MOD_ID);

    public static final DeferredHolder<PanelConnection<?>, PanelConnection<FilterItemStack>> FILTER = CONNECTIONS.register("filter", PanelConnection::new);
    public static final DeferredHolder<PanelConnection<?>, PanelConnection<Integer>> REDSTONE = CONNECTIONS.register("redstone", PanelConnection::new);
    public static final DeferredHolder<PanelConnection<?>, PanelConnection<Integer>> INTEGER = CONNECTIONS.register("integer", PanelConnection::new);
    public static final DeferredHolder<PanelConnection<?>, PanelConnection<String>> STRING = CONNECTIONS.register("string", PanelConnection::new);

    /**
     * returns the value of a connection from a specific factory panel
     * */
    public static <T> Optional<T> getConnectionValue(FactoryPanelBehaviour behaviour, DeferredHolder<PanelConnection<?>, PanelConnection<T>> panelConnection) {
        return getConnectionValue(behaviour, panelConnection.get());
    }

    /**
     * returns the value of a connection from a specific factory panel
     * */
    public static <T> Optional<T> getConnectionValue(FactoryPanelBehaviour behaviour, PanelConnection<T> panelConnection) {
        if(behaviour == null) return Optional.empty();
        if(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.getConnectionValue(panelConnection);
        if(!FACTORY_CONNECTIONS.containsKey(panelConnection)) return Optional.empty();
        //noinspection unchecked
        return (Optional<T>) Optional.of(FACTORY_CONNECTIONS.get(panelConnection).apply(behaviour));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        CONNECTIONS.register(eventBus);
    }

    public static void initDefaults() {
        FACTORY_CONNECTIONS.clear();
        FACTORY_CONNECTIONS.put(FILTER.get(), FilteringBehaviour::getFilter);
        FACTORY_CONNECTIONS.put(REDSTONE.get(), b -> b.satisfied && b.count != 0 ? 15 : 0);
        FACTORY_CONNECTIONS.put(INTEGER.get(), FactoryPanelBehaviour::getLevelInStorage);
        FACTORY_CONNECTIONS.put(STRING.get(), b -> {
            var source = AllDisplaySources.GAUGE_STATUS.get().createEntry(b.getWorld(), b.getPanelPosition());
            return source == null ? null : source.getFirst() + source.getValue().getString();
        });
    }

    public static Collection<PanelConnection<?>> getConnections(FactoryPanelBehaviour at) {
        if(at instanceof AbstractPanelBehaviour ab) return ab.getConnections();
        return FACTORY_CONNECTIONS.keySet();
    }
}
