package net.liukrast.eg;

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
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@Mod(EGConstants.MOD_ID)
public class EG {

    public EG(IEventBus modEventBus, ModContainer modContainer) {
        EGItems.register(modEventBus);
        EGPanels.register(modEventBus);
        EGCreativeModeTabs.register(modEventBus);
        EGBlocks.register(modEventBus);
        EGBlockEntityTypes.register(modEventBus);
        EGDisplayTargets.register(modEventBus);
        modEventBus.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, ExtraGaugesConfig.SPEC);
        EGPackets.register();
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
}
