package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.RegisterCreativeModeTabs;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPanels;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges() {
        @SuppressWarnings("removal") var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegisterItems.register(modEventBus);
        RegisterPanels.register(modEventBus);
        RegisterCreativeModeTabs.register(modEventBus);
        modEventBus.register(this);
    }

    @SubscribeEvent
    public void fMLClientSetup(FMLClientSetupEvent event) {
        RegisterPartialModels.init();
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        GaugeRegistry.PANEL_REGISTRY = event.create(RegistryBuilder.of(ExtraGauges.id("panels"))); //TODO: Is this correct?
    }
}
