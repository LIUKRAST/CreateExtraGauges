package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.logistics.board.PanelBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtraGauges.MOD_ID);

    public static final RegistryObject<PanelBlockItem> LOGIC_GAUGE = ITEMS.register("logic_gauge", () -> new PanelBlockItem(RegisterPanels.LOGIC::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> INT_GAUGE = ITEMS.register("integer_gauge", () -> new PanelBlockItem(RegisterPanels.INT::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> COMPARATOR_GAUGE = ITEMS.register("comparator_gauge", () -> new PanelBlockItem(RegisterPanels.COMPARATOR::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> COUNTER_GAUGE = ITEMS.register("counter_gauge", () -> new PanelBlockItem(RegisterPanels.COUNTER::get, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
