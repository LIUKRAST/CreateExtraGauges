package net.liukrast.eg.content.logistics;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class FactoryPanelDisplayTarget extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        if(!(context.blockEntity() instanceof DisplayCollectorBlockEntity collector)) return;
        List<FactoryPanelPosition> panels = collector.factoryPanelSupport.getLinkedPanels();
        if(panels.isEmpty()) return;
        collector.setComponent(text.get(0));
        collector.factoryPanelSupport.notifyPanels();
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        if(!(context.blockEntity() instanceof DisplayCollectorBlockEntity collector)) return null;
        return collector.factoryPanelSupport.getLinkedPanels()
                .stream()
                .allMatch(pos -> FactoryPanelBehaviour.at(context.level(), pos) instanceof StringPanelBehaviour)
                ? new DisplayTargetStats(1, 100, this) : null;
    }

    @Override
    public boolean requiresComponentSanitization() {
        return true;
    }
}
