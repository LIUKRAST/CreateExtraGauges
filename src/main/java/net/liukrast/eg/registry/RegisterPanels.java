package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.content.logistics.board.IntPanelBehaviour;
import net.liukrast.eg.content.logistics.board.LogicPanelBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterPanels {
    public static final DeferredRegister<PanelType<?>> PANELS = DeferredRegister.create(GaugeRegistry.PANEL_REGISTRY, ExtraGauges.MOD_ID);

    public static final DeferredHolder<PanelType<?>, PanelType<LogicPanelBehaviour>> LOGIC = PANELS.register("logic", () -> new PanelType<>(LogicPanelBehaviour::new, LogicPanelBehaviour.class));
    public static final DeferredHolder<PanelType<?>, PanelType<IntPanelBehaviour>> INT = PANELS.register("integer", () -> new PanelType<>(IntPanelBehaviour::new, IntPanelBehaviour.class));

    public static void register(IEventBus eventBus) {
        PANELS.register(eventBus);
    }
}
