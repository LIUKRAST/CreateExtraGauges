package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PanelConnections {
    public static final PanelConnection<ItemStack> FILTER = new PanelConnection<>() {
        @Override
        public int getColor(ItemStack from, ItemStack to) {
            return 0; //TODO: This should return the actual connection color
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

    public static <T> Optional<T> getConnectionValue(FactoryPanelBehaviour behaviour, PanelConnection<T> panelConnection) {
        if(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.getConnectionValue(panelConnection);
        if(panelConnection == FILTER) //noinspection unchecked
            return Optional.of((T)behaviour.getFilter());
        if(panelConnection == INTEGER)
            //noinspection unchecked
            return (Optional<T>) Optional.of(behaviour.count); //TODO: Doesnt work
        if(panelConnection == REDSTONE) //noinspection unchecked
            return (Optional<T>) Optional.of(behaviour.satisfied && behaviour.count != 0 ? 15 : 0);
        return Optional.empty();
    }

    public static Map<PanelConnection<?>, Supplier<?>> getConnections(FactoryPanelBehaviour behaviour) {
        if(behaviour instanceof AbstractPanelBehaviour ab) return ab.getConnections();
        Map<PanelConnection<?>, Supplier<?>> connectionMap = new HashMap<>();
        connectionMap.put(FILTER, behaviour::getFilter);
        connectionMap.put(INTEGER, () -> behaviour.count);
        connectionMap.put(REDSTONE, () -> behaviour.satisfied && behaviour.count != 0 ? 15 : 0);
        return connectionMap;
    }
}
