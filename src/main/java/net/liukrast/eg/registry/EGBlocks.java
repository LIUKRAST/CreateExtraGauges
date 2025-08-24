package net.liukrast.eg.registry;

import com.simibubi.create.AllBlocks;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.content.item.DisplayCollectorBlockItem;
import net.liukrast.eg.content.logistics.IntSelectorBlock;
import net.liukrast.eg.content.logistics.DisplayCollectorBlock;
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
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EGConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EGConstants.MOD_ID);

    public static final RegistryObject<IntSelectorBlock> INT_SELECTOR = BLOCKS.register("integer_selector", () -> new IntSelectorBlock(BlockBehaviour.Properties.copy(AllBlocks.ANALOG_LEVER.get())));
    public static final RegistryObject<DisplayCollectorBlock> DISPLAY_COLLECTOR = BLOCKS.register("display_collector", () -> new DisplayCollectorBlock(BlockBehaviour.Properties.copy(AllBlocks.DISPLAY_LINK.get())));

    static {
        ITEMS.register("integer_selector", () -> new BlockItem(INT_SELECTOR.get(), new Item.Properties()));
        ITEMS.register("display_collector", () -> new DisplayCollectorBlockItem(DISPLAY_COLLECTOR.get(), new Item.Properties()));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
