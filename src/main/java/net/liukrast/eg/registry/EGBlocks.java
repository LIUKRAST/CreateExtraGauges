package net.liukrast.eg.registry;

import com.simibubi.create.AllBlocks;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.content.item.DisplayCollectorBlockItem;
import net.liukrast.eg.content.logistics.IntSelectorBlock;
import net.liukrast.eg.content.logistics.DisplayCollectorBlock;
import net.liukrast.eg.content.logistics.LinkedButtonBlock;
import net.liukrast.eg.content.logistics.LinkedLeverBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class EGBlocks {
    private EGBlocks() {}
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EGConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EGConstants.MOD_ID);

    public static final RegistryObject<IntSelectorBlock> INT_SELECTOR = BLOCKS.register("integer_selector", () -> new IntSelectorBlock(BlockBehaviour.Properties.copy(AllBlocks.ANALOG_LEVER.get())));
    public static final RegistryObject<DisplayCollectorBlock> DISPLAY_COLLECTOR = BLOCKS.register("display_collector", () -> new DisplayCollectorBlock(BlockBehaviour.Properties.copy(AllBlocks.DISPLAY_LINK.get())));
    public static final RegistryObject<LinkedLeverBlock> LINKED_LEVER = BLOCKS.register("linked_lever", () -> new LinkedLeverBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<LinkedLeverBlock> LINKED_BUTTON = BLOCKS.register("linked_button", () -> new LinkedButtonBlock(BlockBehaviour.Properties.of()));


    static {
        ITEMS.register("integer_selector", () -> new BlockItem(INT_SELECTOR.get(), new Item.Properties()));
        ITEMS.register("display_collector", () -> new DisplayCollectorBlockItem(DISPLAY_COLLECTOR.get(), new Item.Properties()));
        ITEMS.register("linked_lever", () -> EGConstants.wrapWithShiftSummary(new BlockItem(LINKED_LEVER.get(), new Item.Properties())));
        ITEMS.register("linked_button", () -> EGConstants.wrapWithShiftSummary(new BlockItem(LINKED_BUTTON.get(), new Item.Properties())));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

    }
}
