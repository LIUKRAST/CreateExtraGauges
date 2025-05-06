package net.liukrast.eg.registry;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateRegistries;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.block.logic.LogicGaugeDisplaySource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterDisplaySources {
    public static final DeferredRegister<DisplaySource> DISPLAY_SOURCES = DeferredRegister.create(CreateRegistries.DISPLAY_SOURCE, ExtraGauges.MOD_ID);

    static {
        DISPLAY_SOURCES.register("logic_gauge_status", () -> {
            var temp = new LogicGaugeDisplaySource();
            DisplaySource.BY_BLOCK.add(RegisterBlocks.LOGIC_GAUGE.get(), temp);
            return temp;
        });
    }

    public static void register(IEventBus eventBus) {
        DISPLAY_SOURCES.register(eventBus);
    }
}
