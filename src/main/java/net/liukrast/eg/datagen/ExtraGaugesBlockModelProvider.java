package net.liukrast.eg.datagen;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.liukrast.deployer.lib.helper.MinecraftHelpers.ModelProvider.Blocks.createPanel;

public class ExtraGaugesBlockModelProvider extends BlockModelProvider {
    public ExtraGaugesBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EGConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createPanel(this, EGItems.LOGIC_GAUGE.get());
        createPanel(this, EGItems.INT_GAUGE.get());
        createPanel(this, EGItems.COMPARATOR_GAUGE.get());
        createPanel(this, EGItems.COUNTER_GAUGE.get());
        createPanel(this, EGItems.PASSIVE_GAUGE.get());
        createPanel(this, EGItems.STRING_GAUGE.get());
    }
}
