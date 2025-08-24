package net.liukrast.eg.datagen;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ExtraGaugesBlockStateProvider extends BlockStateProvider {
    public ExtraGaugesBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, EGConstants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalFaceBlock(EGBlocks.INT_SELECTOR.get(), models().getExistingFile(EGConstants.id("block/integer_selector")));
    }
}
