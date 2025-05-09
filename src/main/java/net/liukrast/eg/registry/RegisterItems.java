package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.logicBoard.LogicPanelBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtraGauges.MOD_ID);

    public static final DeferredItem<LogicPanelBlockItem> LOGIC_GAUGE = ITEMS.register("logic_gauge", () -> new LogicPanelBlockItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
