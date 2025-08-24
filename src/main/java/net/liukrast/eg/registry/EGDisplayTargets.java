package net.liukrast.eg.registry;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.content.logistics.FactoryPanelDisplayTarget;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class EGDisplayTargets {
    private EGDisplayTargets() {}

    private static final DeferredRegister<DisplayTarget> DISPLAY_TARGETS = DeferredRegister.create(CreateRegistries.DISPLAY_TARGET, EGConstants.MOD_ID);

    static {
        DISPLAY_TARGETS.register("factory_panel", () -> {
            var target = new FactoryPanelDisplayTarget();
            DisplayTarget.BY_BLOCK_ENTITY.register(AllBlockEntityTypes.FACTORY_PANEL.get(), target);
            return target;
        });
    }

    public static void register(IEventBus eventBus) {
        DISPLAY_TARGETS.register(eventBus);
    }
}
