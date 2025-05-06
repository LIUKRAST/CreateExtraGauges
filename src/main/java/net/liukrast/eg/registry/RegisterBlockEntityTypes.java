package net.liukrast.eg.registry;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExtraGauges.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LogicGaugeBlockEntity>> LOGIC_GAUGE = BLOCK_ENTITY_TYPES.register("logic_gauge", () -> BlockEntityType.Builder.of(LogicGaugeBlockEntity::new, RegisterBlocks.LOGIC_GAUGE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

}
