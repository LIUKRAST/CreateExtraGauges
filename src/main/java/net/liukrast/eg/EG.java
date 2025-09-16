package net.liukrast.eg;

import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.datagen.*;
import net.liukrast.eg.registry.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.Collections;
import java.util.List;

@Mod(EGConstants.MOD_ID)
public class EG {

    public EG(IEventBus modEventBus, ModContainer modContainer) {
        EGItems.register(modEventBus);
        EGPanels.register(modEventBus);
        EGCreativeModeTabs.register(modEventBus);
        EGPanelConnections.register(modEventBus);
        EGBlocks.register(modEventBus);
        EGBlockEntityTypes.register(modEventBus);
        EGDisplayTargets.register(modEventBus);
        modEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, ExtraGaugesConfig.SPEC);
        RegisterPackets.register();
        NeoForge.EVENT_BUS.addListener(this::loadLevel);
    }

    private void loadLevel(LevelEvent.Load event) {
        EGExtraPanelConnections.register();
        EGPanelConnections.initDefaults();
    }

    @SubscribeEvent
    private void fmlCommonSetup(FMLCommonSetupEvent event) {
        //SafeNbtWriterRegistry.REGISTRY.register(AllBlockEntityTypes.FACTORY_PANEL.get(), (a,b,c) -> {});
    }

    @SubscribeEvent
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        var provider = event.getLookupProvider();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockStateProvider(packOutput, helper));
        generator.addProvider(event.includeServer(), new ExtraGaugesRecipeProvider(packOutput, provider));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(ExtraGaugesLootTableProvider::new, LootContextParamSets.BLOCK)
        ), provider));
    }

    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        event.register(EGRegistries.PANEL_REGISTRY);
        event.register(EGRegistries.PANEL_CONNECTION_REGISTRY);
    }
}
