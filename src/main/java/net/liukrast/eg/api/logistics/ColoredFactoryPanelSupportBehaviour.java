package net.liukrast.eg.api.logistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import java.util.function.*;

public class ColoredFactoryPanelSupportBehaviour extends FactoryPanelSupportBehaviour {
    private final Function<FactoryPanelBehaviour, Line> colorProvider;

    public ColoredFactoryPanelSupportBehaviour(Function<FactoryPanelBehaviour, Line> colorProvider, SmartBlockEntity be, Supplier<Boolean> isOutput, Supplier<Boolean> outputPower, Runnable onNotify) {
        super(be, isOutput, outputPower, onNotify);
        this.colorProvider = colorProvider;
    }

    public Line getColor(FactoryPanelBehaviour behaviour) {
        return colorProvider.apply(behaviour);
    }

    public record Line(int color, boolean dots) {}
}
