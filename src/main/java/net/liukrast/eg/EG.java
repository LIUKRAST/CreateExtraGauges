package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.datagen.*;
import net.liukrast.eg.registry.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.List;
import java.util.Set;

@Mod(EGConstants.MOD_ID)
public class EG {

    public EG(FMLJavaModLoadingContext ctx) {
        var modEventBus = ctx.getModEventBus();
        EGItems.register(modEventBus);
        EGPanels.register(modEventBus);
        EGCreativeModeTabs.register(modEventBus);
        EGPanelConnections.register(modEventBus);
        EGBlocks.register(modEventBus);
        EGBlockEntityTypes.register(modEventBus);
        EGDisplayTargets.register(modEventBus);
        modEventBus.register(this);
        ctx.registerConfig(ModConfig.Type.COMMON, ExtraGaugesConfig.SPEC);
        RegisterPackets.registerPackets();
        MinecraftForge.EVENT_BUS.addListener(this::loadLevel);

        //Client INIT
        var bus = ctx.getModEventBus();
        //Partial models must be initialized on mod loading cause flywheel is mad
        EGPartialModels.init();
        bus.register(this);
        bus.addListener(EGBlockEntityTypes::registerRenderers);
        ctx.registerConfig(ModConfig.Type.CLIENT, ExtraGaugesConfig.CLIENT_SPEC);
    }

    public void loadLevel(LevelEvent.Load event) {
        EGExtraPanelConnections.register();
        EGPanelConnections.initDefaults();
    }


    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new ExtraGaugesBlockStateProvider(packOutput, helper));
        generator.addProvider(event.includeServer(), new ExtraGaugesRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ExtraGaugesLootTableProvider::new, LootContextParamSets.BLOCK)
        )));
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        EGRegistries.PANEL_REGISTRY = event.create(RegistryBuilder.of(EGConstants.id("panels")));
        EGRegistries.PANEL_CONNECTION_REGISTRY = event.create(RegistryBuilder.of(EGConstants.id("panel_connections")));
    }

    @SubscribeEvent
    public void fMLClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }
}
