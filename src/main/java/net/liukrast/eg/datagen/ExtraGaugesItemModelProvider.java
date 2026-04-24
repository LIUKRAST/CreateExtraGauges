package net.liukrast.eg.datagen;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.liukrast.deployer.lib.helper.MinecraftHelpers.ModelProvider.Items.createPanel;


public class ExtraGaugesItemModelProvider extends ItemModelProvider {
    public ExtraGaugesItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExtraGauges.CONSTANTS.getModId(), existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createPanel(this, EGItems.LOGIC_GAUGE.get());
        createPanel(this, EGItems.INT_GAUGE.get());
        createPanel(this, EGItems.COMPARATOR_GAUGE.get());
        createPanel(this, EGItems.COUNTER_GAUGE.get());
        createPanel(this, EGItems.PASSIVE_GAUGE.get());
        createPanel(this, EGItems.STRING_GAUGE.get());
        createPanel(this, EGItems.EXPRESSION_GAUGE.get());
        createPanel(this, EGItems.FILTER_GAUGE.get());
        withExistingParent("integer_selector", ExtraGauges.CONSTANTS.id("block/integer_selector"));
    }
}
