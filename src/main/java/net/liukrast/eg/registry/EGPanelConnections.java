package net.liukrast.eg.registry;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class EGPanelConnections {
    private EGPanelConnections() {}
    static final Map<ResourceLocation, Function<FactoryPanelBehaviour, ?>> FACTORY_CONNECTIONS = new Reference2ObjectArrayMap<>();
    private static final DeferredRegister<PanelConnection<?>> CONNECTIONS = DeferredRegister.create(EGRegistries.PANEL_CONNECTION_REGISTRY, ExtraGauges.MOD_ID);

    public static final DeferredHolder<PanelConnection<?>, PanelConnection<ItemStack>> FILTER = CONNECTIONS.register("filter", () -> new PanelConnection<>((from, to) -> 0, ItemStack.class));
    public static final DeferredHolder<PanelConnection<?>, PanelConnection<Integer>> REDSTONE = CONNECTIONS.register("redstone", () -> new PanelConnection<>((from, to) -> to > 0 ? 0xEF0000 : 0x580101, Integer.class));
    public static final DeferredHolder<PanelConnection<?>, PanelConnection<Integer>> INTEGER = CONNECTIONS.register("integer", () -> new PanelConnection<>((from, to) -> 0x006496, Integer.class));


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
        if(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.getConnectionValue(panelConnection);
        var key = EGRegistries.PANEL_CONNECTION_REGISTRY.getKey(panelConnection);
        if(!FACTORY_CONNECTIONS.containsKey(key)) return Optional.empty();
        //noinspection unchecked
        return (Optional<T>) Optional.of(FACTORY_CONNECTIONS.get(key).apply(behaviour));
    }

    /**
     * Returns a map of all connections and their values
     * */
    public static Map<PanelConnection<?>, Supplier<?>> getConnections(FactoryPanelBehaviour behaviour) {
        if(behaviour instanceof AbstractPanelBehaviour ab) return ab.getConnections();
        Map<PanelConnection<?>, Supplier<?>> map = new HashMap<>();
        for(var key : FACTORY_CONNECTIONS.keySet()) {
            map.put(EGRegistries.PANEL_CONNECTION_REGISTRY.get(key), () -> FACTORY_CONNECTIONS.get(key).apply(behaviour));
        }
        return map;
    }

    public static <T> T getCap(Level level, BlockPos pos, DeferredHolder<PanelConnection<?>, PanelConnection<T>> connection) {
        return getCap(level, pos, connection.get());
    }

    public static <T> T getCap(Level level, BlockPos pos, PanelConnection<T> connection) {
        return level.getCapability(connection.asCapability(), pos, PanelConnection.PanelContext.from(level.getBlockState(pos)));
    }

    static {
        FACTORY_CONNECTIONS.put(ExtraGauges.id("filter"), FilteringBehaviour::getFilter);
        FACTORY_CONNECTIONS.put(ExtraGauges.id("integer"), FactoryPanelBehaviour::getLevelInStorage);
        FACTORY_CONNECTIONS.put(ExtraGauges.id("redstone"), b -> b.satisfied && b.count != 0 ? 15 : 0);
        NeoForge.EVENT_BUS.post(new AddFactoryConnectionEvent());
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        CONNECTIONS.register(eventBus);
    }
}
