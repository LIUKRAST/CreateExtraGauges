package net.liukrast.eg.registry;

import com.simibubi.create.AllBlocks;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.IntSelectorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGBlocks {
    private EGBlocks() {}
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExtraGauges.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtraGauges.MOD_ID);

    public static final DeferredBlock<IntSelectorBlock> INT_SELECTOR = BLOCKS.register("integer_selector", () -> new IntSelectorBlock(BlockBehaviour.Properties.ofFullCopy(AllBlocks.ANALOG_LEVER.get())));

    static {
        ITEMS.register("integer_selector", () -> new BlockItem(INT_SELECTOR.get(), new Item.Properties()));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
