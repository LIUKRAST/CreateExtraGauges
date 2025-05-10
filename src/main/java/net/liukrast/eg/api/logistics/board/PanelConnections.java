package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class PanelConnections {
    public static final PanelConnection<ItemStack> FILTER = new PanelConnection<>();
    public  static final PanelConnection<Boolean> REDSTONE = new PanelConnection<>();

    public static <T> Optional<T> getConnectionValue(FactoryPanelBehaviour behaviour, PanelConnection<T> panelConnection) {
        if(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour) return abstractPanelBehaviour.getConnectionValue(panelConnection);
        if(panelConnection == FILTER) //noinspection unchecked
            return Optional.of((T)behaviour.getFilter());
        if(panelConnection == REDSTONE) //noinspection unchecked
            return (Optional<T>) Optional.of(behaviour.satisfied); //TODO: is this right?
        return Optional.empty();
    }

}
