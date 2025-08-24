package net.liukrast.eg;

import com.simibubi.create.AllBlocks;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.eg.content.ponder.scenes.highLogistics.*;
import net.liukrast.eg.datagen.ExtraGaugesPonderTagProvider;
import net.liukrast.eg.registry.EGBlocks;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class ExtraGaugesPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return EGConstants.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.forComponents(EGItems.LOGIC_GAUGE.get())
                .addStoryBoard("high_logistics/logic_gauge", LogicGaugeScene::logicGauge)
                .addStoryBoard("high_logistics/logic_gauge_storage", LogicGaugeScene::logicGaugeStorage);
        HELPER.forComponents(EGItems.INT_GAUGE.get(), EGBlocks.INT_SELECTOR.get().asItem())
                .addStoryBoard("high_logistics/integer_gauge", IntGaugeScene::intGauge)
                .addStoryBoard("high_logistics/integer_gauge_storage", IntGaugeScene::intGaugeStorage);
        HELPER.forComponents(EGItems.COUNTER_GAUGE.get())
                .addStoryBoard("high_logistics/counter_gauge", CounterGaugeScene::countGauge);
        HELPER.forComponents(EGItems.COMPARATOR_GAUGE.get())
                .addStoryBoard("high_logistics/comparator_gauge", ComparatorGaugeScene::comparatorGauge);
        HELPER.forComponents(EGItems.PASSIVE_GAUGE.get())
                .addStoryBoard("high_logistics/passive_gauge", PassiveGaugeScene::passiveGauge)
                .addStoryBoard("high_logistics/expanded_factory_recipes", ExtendedCraftScene::autoCrafter);
        HELPER.forComponents(EGItems.STRING_GAUGE.get())
                .addStoryBoard("high_logistics/string_gauge", StringGaugeScenes::stringGauge)
                .addStoryBoard("high_logistics/string_gauge_storage", StringGaugeScenes::stringGaugeStorage)
                .addStoryBoard("high_logistics/display_collector", StringGaugeScenes::displayCollector);
        HELPER.forComponents(EGBlocks.DISPLAY_COLLECTOR.get().asItem())
                .addStoryBoard("high_logistics/display_collector", StringGaugeScenes::displayCollector);
        HELPER.forComponents(AllBlocks.FACTORY_GAUGE.asItem())
                .addStoryBoard("high_logistics/expanded_factory_recipes", ExtendedCraftScene::autoCrafter);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        ExtraGaugesPonderTagProvider.register(helper);
    }
}
