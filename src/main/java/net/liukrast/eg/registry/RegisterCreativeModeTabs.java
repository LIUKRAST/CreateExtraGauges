package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ExtraGauges.MOD_ID);

    static {
        CREATIVE_MODE_TAB.register("main_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.extra_gauges"))
                .icon(RegisterItems.LOGIC_GAUGE.get()::getDefaultInstance)
                .displayItems((pars, out) -> {
                    out.accept(RegisterItems.LOGIC_GAUGE);
                    out.accept(RegisterItems.INT_GAUGE);
                    out.accept(RegisterItems.COMPARATOR_GAUGE);
                    out.accept(RegisterItems.COUNTER_GAUGE);
                })
                .build());
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TAB.register(modEventBus);
    }
}
