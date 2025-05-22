package net.liukrast.eg;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.eg.content.ponder.scenes.highLogistics.ComparatorGaugeScene;
import net.liukrast.eg.content.ponder.scenes.highLogistics.CounterGaugeScene;
import net.liukrast.eg.content.ponder.scenes.highLogistics.IntGaugeScene;
import net.liukrast.eg.content.ponder.scenes.highLogistics.LogicGaugeScene;
import net.liukrast.eg.datagen.ExtraGaugesPonderTagProvider;
import net.liukrast.eg.registry.RegisterItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class ExtraGaugesPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return ExtraGauges.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.forComponents(RegisterItems.LOGIC_GAUGE.get())
                .addStoryBoard("high_logistics/logic_gauge", LogicGaugeScene::logicGauge);
        HELPER.forComponents(RegisterItems.INT_GAUGE.get())
                .addStoryBoard("high_logistics/integer_gauge_redstone", IntGaugeScene::intGaugeRedstone)
                .addStoryBoard("high_logistics/integer_gauge_factory", IntGaugeScene::intGaugeFactory);
        HELPER.forComponents(RegisterItems.COUNTER_GAUGE.get())
                .addStoryBoard("high_logistics/counter_gauge", CounterGaugeScene::countGauge);
        HELPER.forComponents(RegisterItems.COMPARATOR_GAUGE.get())
                .addStoryBoard("high_logistics/comparator_gauge_redstone", ComparatorGaugeScene::compGaugeRedstone)
                .addStoryBoard("high_logistics/comparator_gauge_factory", ComparatorGaugeScene::compGaugeFactory);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        ExtraGaugesPonderTagProvider.register(helper);
    }
}
