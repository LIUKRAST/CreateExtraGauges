package net.liukrast.eg.registry;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.link.FactoryPanelDisplayTarget;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EGDisplayTargets {
    private EGDisplayTargets() {}

    private static final DeferredRegister<DisplayTarget> REGISTER = DeferredRegister.create(CreateRegistries.DISPLAY_TARGET, ExtraGauges.CONSTANTS.getModId());

    static {
        REGISTER.register("factory_panel", () -> {
            var target = new FactoryPanelDisplayTarget();
            DisplayTarget.BY_BLOCK_ENTITY.register(AllBlockEntityTypes.FACTORY_PANEL.get(), target);
            return target;
        });
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
