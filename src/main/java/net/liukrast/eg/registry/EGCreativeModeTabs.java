package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGCreativeModeTabs {
    private EGCreativeModeTabs() {}

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ExtraGauges.MOD_ID);

    static {
        CREATIVE_MODE_TAB.register("main_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.extra_gauges"))
                .icon(EGItems.LOGIC_GAUGE.get()::getDefaultInstance)
                .displayItems((pars, out) -> {
                    out.accept(EGItems.LOGIC_GAUGE);
                    out.accept(EGItems.INT_GAUGE);
                    out.accept(EGItems.COMPARATOR_GAUGE);
                    out.accept(EGItems.COUNTER_GAUGE);
                })
                .build());
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TAB.register(modEventBus);
    }
}
