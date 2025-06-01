package net.liukrast.eg.datagen;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ExtraGaugesBlockStateProvider extends BlockStateProvider {
    public ExtraGaugesBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExtraGauges.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalFaceBlock(EGBlocks.INT_SELECTOR.get(), models().getExistingFile(ExtraGauges.id("block/integer_selector")));
    }
}
