package net.liukrast.eg.datagen;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Function;

public class ExtraGaugesBlockModelProvider extends BlockModelProvider {
    public ExtraGaugesBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExtraGauges.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createGauge(EGItems.LOGIC_GAUGE.get());
        createGauge(EGItems.INT_GAUGE.get());
        createGauge(EGItems.COMPARATOR_GAUGE.get());
        createGauge(EGItems.COUNTER_GAUGE.get());
        createGauge(EGItems.PASSIVE_GAUGE.get());
        createGauge(EGItems.STRING_GAUGE.get());
    }

    public static BlockModelBuilder createGauge(BlockModelProvider instance, Item item, Function<String, String> texture) {
        var id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return instance.getBuilder(id.toString()).parent(new ModelFile.UncheckedModelFile(ExtraGauges.id("block/template_gauge")))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())));
    }

    @SuppressWarnings("unused")
    public static BlockModelBuilder createGauge(BlockModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static BlockModelBuilder createPanel(BlockModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id.split("_")[0] + "_panel");
    }

    private void createGauge(Item item) {
        createPanel(this, item);
    }
}
