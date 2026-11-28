package net.liukrast.eg.registry;

import net.liukrast.deployer.lib.logistics.board.LogisticallyLinkedPanelBlockItem;
import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.eg.ExtraGauges;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGItems {
    private EGItems() {}
    private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtraGauges.CONSTANTS.getModId());

    public static final DeferredItem<PanelBlockItem> LOGIC_GAUGE = REGISTER.register("logic_gauge", () -> new PanelBlockItem(EGPanels.LOGIC::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> INT_GAUGE = REGISTER.register("integer_gauge", () -> new PanelBlockItem(EGPanels.INT::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> COMPARATOR_GAUGE = REGISTER.register("comparator_gauge", () -> new PanelBlockItem(EGPanels.COMPARATOR::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> COUNTER_GAUGE = REGISTER.register("counter_gauge", () -> new PanelBlockItem(EGPanels.COUNTER::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> PASSIVE_GAUGE = REGISTER.register("passive_gauge", () -> new LogisticallyLinkedPanelBlockItem(EGPanels.PASSIVE::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> STRING_GAUGE = REGISTER.register("string_gauge", () -> new PanelBlockItem(EGPanels.STRING::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> EXPRESSION_GAUGE = REGISTER.register("expression_gauge", () -> new PanelBlockItem(EGPanels.EXPRESSION::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> FILTER_GAUGE = REGISTER.register("filter_gauge", () -> new LogisticallyLinkedPanelBlockItem(EGPanels.FILTER::get, new Item.Properties()));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
