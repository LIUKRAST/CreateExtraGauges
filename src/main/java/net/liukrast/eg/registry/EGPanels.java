package net.liukrast.eg.registry;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.content.logistics.board.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class EGPanels {
    private EGPanels() {}
    private static final DeferredRegister<PanelType<?>> PANELS = DeferredRegister.create(EGRegistries.PANEL_REGISTRY_KEY, EGConstants.MOD_ID);

    public static final RegistryObject<PanelType<LogicPanelBehaviour>> LOGIC = PANELS.register("logic", () -> new PanelType<>(LogicPanelBehaviour::new, LogicPanelBehaviour.class));
    public static final RegistryObject<PanelType<IntPanelBehaviour>> INT = PANELS.register("integer", () -> new PanelType<>(IntPanelBehaviour::new, IntPanelBehaviour.class));
    public static final RegistryObject<PanelType<ComparatorPanelBehaviour>> COMPARATOR = PANELS.register("comparator", () -> new PanelType<>(ComparatorPanelBehaviour::new, ComparatorPanelBehaviour.class));
    public static final RegistryObject<PanelType<CounterPanelBehaviour>> COUNTER = PANELS.register("counter", () -> new PanelType<>(CounterPanelBehaviour::new, CounterPanelBehaviour.class));
    public static final RegistryObject<PanelType<PassivePanelBehaviour>> PASSIVE = PANELS.register("passive", () -> new PanelType<>(PassivePanelBehaviour::new, PassivePanelBehaviour.class));
    public static final RegistryObject<PanelType<StringPanelBehaviour>> STRING = PANELS.register("string", () -> new PanelType<>(StringPanelBehaviour::new, StringPanelBehaviour.class));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        PANELS.register(eventBus);
    }
}
