package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class RegisterCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ExtraGauges.MOD_ID);

    static {
        CREATIVE_MODE_TAB.register("main_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.extra_gauges"))
                .icon(RegisterItems.LOGIC_GAUGE.get()::getDefaultInstance)
                .displayItems((pars, out) -> {
                    out.accept(RegisterItems.LOGIC_GAUGE.get());
                    out.accept(RegisterItems.INT_GAUGE.get());
                    out.accept(RegisterItems.COMPARATOR_GAUGE.get());
                    out.accept(RegisterItems.COUNTER_GAUGE.get());
                })
                .build());
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TAB.register(modEventBus);
    }
}
