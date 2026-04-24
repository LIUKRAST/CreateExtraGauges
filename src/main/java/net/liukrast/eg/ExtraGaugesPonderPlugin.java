package net.liukrast.eg;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.deployer.lib.helper.ponder.SmartMultiSceneBuilder;
import net.liukrast.deployer.lib.helper.ponder.SmartPonderRegistrationHelper;
import net.liukrast.eg.content.ponder.scenes.highLogistics.*;
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
        return ExtraGauges.CONSTANTS.getModId();
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        SmartPonderRegistrationHelper<Item> HELPER = SmartPonderRegistrationHelper.of(helper.withKeyFunction(BuiltInRegistries.ITEM::getKey));

        HELPER.forComponents(EGItems.LOGIC_GAUGE.get())
                .addPonder(new LogicGaugePonder());
        HELPER.forComponents(EGItems.INT_GAUGE.get())
                .addPonder(new IntGaugePonder())
                .addPonder(new IntGaugeMemoryPonder());
        HELPER.forComponents(EGItems.COMPARATOR_GAUGE.get())
                .addPonder(new ComparatorGaugePonder());
        HELPER.forComponents(EGItems.COUNTER_GAUGE.get())
                .addPonder(new CounterGaugePonder());
        HELPER.forComponents(EGItems.STRING_GAUGE.get())
                .addPonder(new StringGaugePonder());
        HELPER.forComponents(EGItems.EXPRESSION_GAUGE.get())
                .addPonder(new ExpressionGaugePonder());
        HELPER.forComponents(EGItems.FILTER_GAUGE.get())
                .addPonder(new FilterGaugePonder());

        // Legacy ponders
        HELPER.getHelper().forComponents(EGItems.PASSIVE_GAUGE.get())
                .addStoryBoard("high_logistics/passive_gauge", PassiveGaugeScene::passiveGauge)
                .addStoryBoard("high_logistics/expanded_factory_recipes", ExtendedCraftScene::autoCrafter);
        HELPER.getHelper().forComponents(EGBlocks.DISPLAY_COLLECTOR.asItem())
                .addStoryBoard("high_logistics/display_collector", StringGaugeScenes::displayCollector);
        HELPER.getHelper().forComponents(AllBlocks.FACTORY_GAUGE.asItem())
                .addStoryBoard("high_logistics/expanded_factory_recipes", ExtendedCraftScene::autoCrafter);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        var HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
                .add(EGItems.LOGIC_GAUGE.get())
                .add(EGItems.INT_GAUGE.get())
                .add(EGItems.COMPARATOR_GAUGE.get())
                .add(EGItems.COUNTER_GAUGE.get())
                .add(EGItems.PASSIVE_GAUGE.get())
                .add(EGItems.EXPRESSION_GAUGE.get());
    }
}
