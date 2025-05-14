package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.logistics.board.PanelBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtraGauges.MOD_ID);

    public static final DeferredItem<PanelBlockItem> LOGIC_GAUGE = ITEMS.register("logic_gauge", () -> new PanelBlockItem(RegisterPanels.LOGIC::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> INT_GAUGE = ITEMS.register("integer_gauge", () -> new PanelBlockItem(RegisterPanels.INT::get, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
