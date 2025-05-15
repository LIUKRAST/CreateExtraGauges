package net.liukrast.eg.datagen;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.eg.registry.RegisterItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ExtraGaugesPonderTagProvider{
    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        var HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
                .add(RegisterItems.LOGIC_GAUGE.get())
                .add(RegisterItems.INT_GAUGE.get())
                .add(RegisterItems.COMPARATOR_GAUGE.get())
                .add(RegisterItems.COUNTER_GAUGE.get());
    }
}
