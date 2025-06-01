package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.IntSelectorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class EGBlockEntityTypes {
    private EGBlockEntityTypes() {}
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExtraGauges.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IntSelectorBlockEntity>> INT_SELECTOR = BLOCK_ENTITY_TYPES.register("integer_selector", () -> BlockEntityType.Builder.of(IntSelectorBlockEntity::new, EGBlocks.INT_SELECTOR.get()).build(null));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
