package net.liukrast.eg.registry;

import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.board.ExpressionPanelBehaviour;
import net.liukrast.eg.content.logistics.board.*;
import net.liukrast.eg.content.logistics.board.comparator.ComparatorPanelBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGPanels {
    private EGPanels() {}
    private static final DeferredRegister<PanelType<?>> PANELS = DeferredRegister.create(DeployerRegistries.PANEL, ExtraGauges.CONSTANTS.getModId());

    public static final DeferredHolder<PanelType<?>, PanelType<LogicPanelBehaviour>> LOGIC = PANELS.register("logic", () -> new PanelType<>(LogicPanelBehaviour::new, LogicPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<IntPanelBehaviour>> INT = PANELS.register("integer", () -> new PanelType<>(IntPanelBehaviour::new, IntPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<ComparatorPanelBehaviour>> COMPARATOR = PANELS.register("comparator", () -> new PanelType<>(ComparatorPanelBehaviour::new, ComparatorPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<CounterPanelBehaviour>> COUNTER = PANELS.register("counter", () -> new PanelType<>(CounterPanelBehaviour::new, CounterPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<PassivePanelBehaviour>> PASSIVE = PANELS.register("passive", () -> new PanelType<>(PassivePanelBehaviour::new, PassivePanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<StringPanelBehaviour>> STRING = PANELS.register("string", () -> new PanelType<>(StringPanelBehaviour::new, StringPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<ExpressionPanelBehaviour>> EXPRESSION = PANELS.register("expression", () -> new PanelType<>(ExpressionPanelBehaviour::new, ExpressionPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<FilterPanelBehaviour>> FILTER = PANELS.register("filter", () -> new PanelType<>(FilterPanelBehaviour::new, FilterPanelBehaviour.class));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        PANELS.register(eventBus);
    }
}
