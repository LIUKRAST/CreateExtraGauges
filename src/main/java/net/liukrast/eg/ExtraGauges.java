package net.liukrast.eg;

import com.simibubi.create.AllCreativeModeTabs;
import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.event.AbstractPanelRenderEvent;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.RegisterCreativeModeTabs;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPanels;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges(IEventBus modEventBus) {
        RegisterItems.register(modEventBus);
        RegisterPanels.register(modEventBus);
        RegisterCreativeModeTabs.register(modEventBus);
        modEventBus.register(this);
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        RegisterPartialModels.init();
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

    @SubscribeEvent
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
    }

    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        event.register(GaugeRegistry.PANEL_REGISTRY);
    }
}
