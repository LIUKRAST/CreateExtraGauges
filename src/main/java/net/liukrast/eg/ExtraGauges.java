package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.datagen.ExtraGaugesBlockModelProvider;
import net.liukrast.eg.datagen.ExtraGaugesBlockStateProvider;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path, Object... args) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format(path, args));
    }

    public ExtraGauges(IEventBus modEventBus, ModContainer modContainer) {
        EGItems.register(modEventBus);
        EGPanels.register(modEventBus);
        EGCreativeModeTabs.register(modEventBus);
        EGPanelConnections.register(modEventBus);
        EGBlocks.register(modEventBus);
        EGBlockEntityTypes.register(modEventBus);
        EGDisplayTargets.register(modEventBus);
        modEventBus.register(this);
        modEventBus.addListener(EGBlockEntityTypes::registerRenderers);
        modContainer.registerConfig(ModConfig.Type.COMMON, ExtraGaugesConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ExtraGaugesConfig.CLIENT_SPEC);
        RegisterPackets.register();
        NeoForge.EVENT_BUS.addListener(this::loadLevel);
    }

    private void loadLevel(LevelEvent.Load event) {
        EGExtraPanelConnections.register();
        EGPanelConnections.initDefaults();
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
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockStateProvider(packOutput, helper));
    }

    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        event.register(EGRegistries.PANEL_REGISTRY);
        event.register(EGRegistries.PANEL_CONNECTION_REGISTRY);
    }
}
