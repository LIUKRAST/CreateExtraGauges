package net.liukrast.eg;

import com.simibubi.create.AllCreativeModeTabs;
import net.liukrast.eg.content.block.logic.LogicGaugeRenderer;
import net.liukrast.eg.registry.RegisterBlockEntityTypes;
import net.liukrast.eg.registry.RegisterBlocks;
import net.liukrast.eg.registry.RegisterDisplaySources;
import net.liukrast.eg.registry.RegisterItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges(IEventBus modEventBus) {
        RegisterBlocks.register(modEventBus);
        RegisterItems.register(modEventBus);
        RegisterBlockEntityTypes.register(modEventBus);
        RegisterDisplaySources.register(modEventBus);
        modEventBus.register(this);
    }

    @SubscribeEvent
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()) event.accept(RegisterBlocks.LOGIC_GAUGE);
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegisterBlockEntityTypes.LOGIC_GAUGE.get(), LogicGaugeRenderer::new);
    }
}
