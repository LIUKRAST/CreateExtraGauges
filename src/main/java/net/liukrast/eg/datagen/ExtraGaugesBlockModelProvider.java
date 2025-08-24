package net.liukrast.eg.datagen;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Function;

public class ExtraGaugesBlockModelProvider extends BlockModelProvider {
    public ExtraGaugesBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EGConstants.MOD_ID, existingFileHelper);
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
        var id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
        return instance.getBuilder(id.toString()).parent(new ModelFile.UncheckedModelFile(EGConstants.id("block/template_gauge")))
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
