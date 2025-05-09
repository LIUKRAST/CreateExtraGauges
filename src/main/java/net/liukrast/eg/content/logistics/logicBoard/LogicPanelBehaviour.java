package net.liukrast.eg.content.logistics.logicBoard;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.world.item.Item;

public class LogicPanelBehaviour extends AbstractPanelBehaviour {

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public Item getItem() {
        return RegisterItems.LOGIC_GAUGE.get();
    }

    @Override
    public boolean mayConnect(FactoryPanelBehaviour other) {
        return true;
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RegisterPartialModels.LOGIC_PANEL;
    }
}
