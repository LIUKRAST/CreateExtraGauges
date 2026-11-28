package net.liukrast.eg.registry;

import com.simibubi.create.content.redstone.displayLink.LinkBulbRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.link.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("DataFlowIssue")
public class EGBlockEntityTypes {
    private EGBlockEntityTypes() {}
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExtraGauges.CONSTANTS.getModId());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IntSelectorBlockEntity>> INT_SELECTOR = REGISTER.register("integer_selector", () -> BlockEntityType.Builder.of(IntSelectorBlockEntity::new, EGBlocks.INT_SELECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayCollectorBlockEntity>> DISPLAY_COLLECTOR = REGISTER.register("display_collector", () -> BlockEntityType.Builder.of(DisplayCollectorBlockEntity::new, EGBlocks.DISPLAY_COLLECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LinkedControlBlockEntity>> LINKED_CONTROL = REGISTER.register("linked_control", () -> BlockEntityType.Builder.of(LinkedControlBlockEntity::new, EGBlocks.LINKED_LEVER.get(), EGBlocks.LINKED_BUTTON.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstonePortBlockEntity>> REDSTONE_PORT = REGISTER.register("redstone_port", () -> BlockEntityType.Builder.of(RedstonePortBlockEntity::new, EGBlocks.REDSTONE_PORT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RoseQuartzPortBlockEntity>> ROSE_QUARTZ_PORT = REGISTER.register("rose_quartz_port", () -> BlockEntityType.Builder.of(RoseQuartzPortBlockEntity::new, EGBlocks.ROSE_QUARTZ_PORT.get()).build(null));

    static {
        REGISTER.addAlias(ExtraGauges.CONSTANTS.id("linked_lever"), ExtraGauges.CONSTANTS.id("linked_control"));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DISPLAY_COLLECTOR.get(), LinkBulbRenderer::new);
        event.registerBlockEntityRenderer(LINKED_CONTROL.get(), SmartBlockEntityRenderer::new);
    }
}
