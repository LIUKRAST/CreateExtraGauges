package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.datagen.ExtraGaugesBlockStateProvider;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges(IEventBus modEventBus) {
        EGItems.register(modEventBus);
        EGPanels.register(modEventBus);
        EGCreativeModeTabs.register(modEventBus);
        EGPanelConnections.register(modEventBus);
        EGBlocks.register(modEventBus);
        EGBlockEntityTypes.register(modEventBus);
        modEventBus.register(this);
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        EGPartialModels.init();
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

    @SubscribeEvent
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockStateProvider(packOutput, helper));
    }

    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        event.register(EGRegistries.PANEL_REGISTRY);
        event.register(EGRegistries.PANEL_CONNECTION_REGISTRY);
    }

    @SubscribeEvent
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        EGCapabilities.registerDefaults(event);
    }
}
