package net.liukrast.eg.registry;

import com.simibubi.create.content.redstone.displayLink.LinkBulbRenderer;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.DisplayCollectorBlockEntity;
import net.liukrast.eg.content.logistics.IntSelectorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("DataFlowIssue")
public class EGBlockEntityTypes {
    private EGBlockEntityTypes() {}
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExtraGauges.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IntSelectorBlockEntity>> INT_SELECTOR = BLOCK_ENTITY_TYPES.register("integer_selector", () -> BlockEntityType.Builder.of(IntSelectorBlockEntity::new, EGBlocks.INT_SELECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayCollectorBlockEntity>> DISPLAY_COLLECTOR = BLOCK_ENTITY_TYPES.register("display_collector", () -> BlockEntityType.Builder.of(DisplayCollectorBlockEntity::new, EGBlocks.DISPLAY_COLLECTOR.get()).build(null));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DISPLAY_COLLECTOR.get(), LinkBulbRenderer::new);
    }
}
