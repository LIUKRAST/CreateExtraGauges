package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtraGauges.MOD_ID);

    static {
        ITEMS.registerSimpleBlockItem("logic_gauge", RegisterBlocks.LOGIC_GAUGE);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
