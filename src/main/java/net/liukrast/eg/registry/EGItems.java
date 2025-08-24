package net.liukrast.eg.registry;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.logistics.board.PanelBlockItem;
import net.liukrast.eg.content.item.LogisticallyLinkedPanelBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class EGItems {
    private EGItems() {}
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EGConstants.MOD_ID);

    public static final RegistryObject<PanelBlockItem> LOGIC_GAUGE = ITEMS.register("logic_gauge", () -> new PanelBlockItem(EGPanels.LOGIC::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> INT_GAUGE = ITEMS.register("integer_gauge", () -> new PanelBlockItem(EGPanels.INT::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> COMPARATOR_GAUGE = ITEMS.register("comparator_gauge", () -> new PanelBlockItem(EGPanels.COMPARATOR::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> COUNTER_GAUGE = ITEMS.register("counter_gauge", () -> new PanelBlockItem(EGPanels.COUNTER::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> PASSIVE_GAUGE = ITEMS.register("passive_gauge", () -> new LogisticallyLinkedPanelBlockItem(EGPanels.PASSIVE::get, new Item.Properties()));
    public static final RegistryObject<PanelBlockItem> STRING_GAUGE = ITEMS.register("string_gauge", () -> new PanelBlockItem(EGPanels.STRING::get, new Item.Properties()));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
