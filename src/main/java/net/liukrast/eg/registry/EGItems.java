package net.liukrast.eg.registry;

import net.liukrast.deployer.lib.logistics.board.LogisticallyLinkedPanelBlockItem;
import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.eg.EGConstants;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGItems {
    private EGItems() {}
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EGConstants.MOD_ID);

    public static final DeferredItem<PanelBlockItem> LOGIC_GAUGE = ITEMS.register("logic_gauge", () -> new PanelBlockItem(EGPanels.LOGIC::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> INT_GAUGE = ITEMS.register("integer_gauge", () -> new PanelBlockItem(EGPanels.INT::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> COMPARATOR_GAUGE = ITEMS.register("comparator_gauge", () -> new PanelBlockItem(EGPanels.COMPARATOR::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> COUNTER_GAUGE = ITEMS.register("counter_gauge", () -> new PanelBlockItem(EGPanels.COUNTER::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> PASSIVE_GAUGE = ITEMS.register("passive_gauge", () -> new LogisticallyLinkedPanelBlockItem(EGPanels.PASSIVE::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> STRING_GAUGE = ITEMS.register("string_gauge", () -> new PanelBlockItem(EGPanels.STRING::get, new Item.Properties()));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
