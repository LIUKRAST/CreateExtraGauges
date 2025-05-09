package net.liukrast.eg.api.registry;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;

public class PanelType<T extends AbstractPanelBehaviour> {
    private final Constructor<T> constructor;
    public PanelType(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    public AbstractPanelBehaviour create(FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        return constructor.apply(this, be, slot);
    }

    public interface Constructor<T extends AbstractPanelBehaviour> {
        T apply(PanelType<T> panelType, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot);
    }
}
