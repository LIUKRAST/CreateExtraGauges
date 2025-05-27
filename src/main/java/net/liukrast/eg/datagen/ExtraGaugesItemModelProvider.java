package net.liukrast.eg.datagen;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class is used by extra gauges to generate models, though, the method {@link ExtraGaugesItemModelProvider#createGauge(ItemModelProvider, Item)} can be used by your mod to generate extra gauges item models.
 * */
public class ExtraGaugesItemModelProvider extends ItemModelProvider {
    public ExtraGaugesItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExtraGauges.MOD_ID, existingFileHelper);
    }

    public static ItemModelBuilder createGauge(ItemModelProvider instance, Item item, Function<String, String> texture) {
        var id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return instance.getBuilder(id.toString()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/factory_gauge/item")))
                .texture("0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())));
    }

    @SuppressWarnings("unused")
    public static ItemModelBuilder createGauge(ItemModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static ItemModelBuilder createPanel(ItemModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id.split("_")[0] + "_panel");
    }

    private void createGauge(Item item) {
        createPanel(this, item);
    }

    @Override
    protected void registerModels() {
        createGauge(EGItems.LOGIC_GAUGE.get());
        createGauge(EGItems.INT_GAUGE.get());
        createGauge(EGItems.COMPARATOR_GAUGE.get());
        createGauge(EGItems.COUNTER_GAUGE.get());
    }
}
