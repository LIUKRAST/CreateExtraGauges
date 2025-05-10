package net.liukrast.eg.api.registry;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;

public class PanelType<T extends AbstractPanelBehaviour> {
    private final Constructor<T> constructor;
    private final Class<T> clazz;
    public PanelType(Constructor<T> constructor, Class<T> clazz) {
        this.constructor = constructor;
        this.clazz = clazz;
    }

    public AbstractPanelBehaviour create(FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        return constructor.apply(this, be, slot);
    }

    public Class<T> asClass() {
        return clazz;
    }

    public interface Constructor<T extends AbstractPanelBehaviour> {
        T apply(PanelType<T> panelType, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot);
    }
}
