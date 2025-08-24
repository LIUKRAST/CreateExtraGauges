package net.liukrast.eg.registry;

import com.simibubi.create.content.redstone.displayLink.LinkBulbRenderer;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.content.logistics.DisplayCollectorBlockEntity;
import net.liukrast.eg.content.logistics.IntSelectorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("DataFlowIssue")
public class EGBlockEntityTypes {
    private EGBlockEntityTypes() {}
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EGConstants.MOD_ID);

    public static final RegistryObject<BlockEntityType<IntSelectorBlockEntity>> INT_SELECTOR = BLOCK_ENTITY_TYPES.register("integer_selector", () -> BlockEntityType.Builder.of(IntSelectorBlockEntity::new, EGBlocks.INT_SELECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<DisplayCollectorBlockEntity>> DISPLAY_COLLECTOR = BLOCK_ENTITY_TYPES.register("display_collector", () -> BlockEntityType.Builder.of(DisplayCollectorBlockEntity::new, EGBlocks.DISPLAY_COLLECTOR.get()).build(null));

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DISPLAY_COLLECTOR.get(), LinkBulbRenderer::new);
    }
}
