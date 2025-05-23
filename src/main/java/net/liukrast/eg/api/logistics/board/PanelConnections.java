package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PanelConnections {
    public static final PanelConnection<ItemStack> FILTER = new PanelConnection<>() {
        @Override
        public int getColor(ItemStack from, ItemStack to) {
            return 0; //TODO: This should return the actual connection color?
        }
    };
    public static final PanelConnection<Integer> REDSTONE = new PanelConnection<>() {
        @Override
        public int getColor(Integer from, Integer to) {
            return to > 0 ? 0xEF0000 : 0x580101;
        }
    };
    public static final PanelConnection<Integer> INTEGER = new PanelConnection<>() {
        @Override
        public int getColor(Integer from, Integer to) {
            return 0x006496;
        }
    };

    static final Map<PanelConnection<?>, Function<FactoryPanelBehaviour, ?>> FACTORY_CONNECTIONS = new Reference2ObjectArrayMap<>();

    public static <T> Optional<T> getConnectionValue(FactoryPanelBehaviour behaviour, PanelConnection<T> panelConnection) {
        if(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.getConnectionValue(panelConnection);
        if(!FACTORY_CONNECTIONS.containsKey(panelConnection)) return Optional.empty();
        //noinspection unchecked
        return (Optional<T>) Optional.of(FACTORY_CONNECTIONS.get(panelConnection).apply(behaviour));
    }

    public static Map<PanelConnection<?>, Supplier<?>> getConnections(FactoryPanelBehaviour behaviour) {
        if(behaviour instanceof AbstractPanelBehaviour ab) return ab.getConnections();
        Map<PanelConnection<?>, Supplier<?>> map = new HashMap<>();
        for(var key : FACTORY_CONNECTIONS.keySet()) {
            map.put(key, () -> FACTORY_CONNECTIONS.get(key).apply(behaviour));
        }
        return map;
    }

    static {
        FACTORY_CONNECTIONS.put(FILTER, FilteringBehaviour::getFilter);
        FACTORY_CONNECTIONS.put(INTEGER, FactoryPanelBehaviour::getLevelInStorage);
        FACTORY_CONNECTIONS.put(REDSTONE, b -> b.satisfied && b.count != 0 ? 15 : 0);
        MinecraftForge.EVENT_BUS.post(new AddFactoryConnectionEvent());
    }
}
