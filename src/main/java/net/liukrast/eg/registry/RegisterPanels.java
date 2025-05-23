package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.content.logistics.board.ComparatorPanelBehaviour;
import net.liukrast.eg.content.logistics.board.CounterPanelBehaviour;
import net.liukrast.eg.content.logistics.board.IntPanelBehaviour;
import net.liukrast.eg.content.logistics.board.LogicPanelBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegisterPanels {
    public static final DeferredRegister<PanelType<?>> PANELS = DeferredRegister.create(GaugeRegistry.PANEL_REGISTRY_KEY, ExtraGauges.MOD_ID);

    public static final RegistryObject<PanelType<LogicPanelBehaviour>> LOGIC = PANELS.register("logic", () -> new PanelType<>(LogicPanelBehaviour::new, LogicPanelBehaviour.class));
    public static final RegistryObject<PanelType<IntPanelBehaviour>> INT = PANELS.register("integer", () -> new PanelType<>(IntPanelBehaviour::new, IntPanelBehaviour.class));
    public static final RegistryObject<PanelType<ComparatorPanelBehaviour>> COMPARATOR = PANELS.register("comparator", () -> new PanelType<>(ComparatorPanelBehaviour::new, ComparatorPanelBehaviour.class));
    public static final RegistryObject<PanelType<CounterPanelBehaviour>> COUNTER = PANELS.register("counter", () -> new PanelType<>(CounterPanelBehaviour::new, CounterPanelBehaviour.class));

    public static void register(IEventBus eventBus) {
        PANELS.register(eventBus);
    }
}
