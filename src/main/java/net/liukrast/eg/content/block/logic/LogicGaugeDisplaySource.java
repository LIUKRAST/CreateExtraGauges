package net.liukrast.eg.content.block.logic;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LogicGaugeDisplaySource extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        List<FactoryPanelPosition> panels = context.blockEntity().factoryPanelSupport.getLinkedPanels();
        if (panels.isEmpty())
            return Stream.empty();
        return panels.stream()
                .map(fpp -> createEntry(context.level(), fpp))
                .filter(Objects::nonNull)
                .limit(maxRows);
    }

    @Nullable
    public IntAttached<MutableComponent> createEntry(Level level, FactoryPanelPosition pos) {
        if(!(level.getBlockEntity(pos.pos()) instanceof LogicGaugeBlockEntity)) return null;
        boolean powered = level.getBlockState(pos.pos()).getValue(LogicGaugeBlock.POWERED);

        String s = powered ? "✔ True" : "▪ False";
        return IntAttached.withZero(Component.literal(s));
    }

    @Override
    protected String getTranslationKey() {
        return "logic_gauge_status";
    }

    @Override
    protected boolean valueFirst() {
        return true;
    }
}
