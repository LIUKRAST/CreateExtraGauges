package net.liukrast.eg.datagen;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ExtraGaugesPonderTagProvider{
    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        var HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
                .add(EGItems.LOGIC_GAUGE.get())
                .add(EGItems.INT_GAUGE.get())
                .add(EGItems.COMPARATOR_GAUGE.get())
                .add(EGItems.COUNTER_GAUGE.get())
                .add(EGItems.PASSIVE_GAUGE.get());
    }
}
