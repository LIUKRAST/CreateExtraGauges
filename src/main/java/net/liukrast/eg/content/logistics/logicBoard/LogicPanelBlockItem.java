package net.liukrast.eg.content.logistics.logicBoard;

import net.liukrast.eg.api.logistics.board.AbstractPanelBlockItem;
import net.liukrast.eg.registry.RegisterPanels;

public class LogicPanelBlockItem extends AbstractPanelBlockItem {
    public LogicPanelBlockItem(Properties properties) {
        super(RegisterPanels.LOGIC::get, properties);
    }
}
