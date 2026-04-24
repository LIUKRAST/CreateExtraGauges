package net.liukrast.eg.datagen;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.logistics.link.RedstonePortBlock;
import net.liukrast.eg.content.logistics.link.RoseQuartzPortBlock;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ExtraGaugesBlockStateProvider extends BlockStateProvider {
    public ExtraGaugesBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExtraGauges.CONSTANTS.getModId(), exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalFaceBlock(EGBlocks.INT_SELECTOR.get(), models().getExistingFile(ExtraGauges.CONSTANTS.id("block/integer_selector")));
        horizontalFaceBlock(EGBlocks.REDSTONE_PORT.get(), state -> {
            if(state.getValue(RedstonePortBlock.OUTPUT) && state.getValue(RedstonePortBlock.POWERED)) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/redstone_port/on_out"));
            if(state.getValue(RedstonePortBlock.OUTPUT) && !state.getValue(RedstonePortBlock.POWERED)) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/redstone_port/off_out"));
            if(!state.getValue(RedstonePortBlock.OUTPUT) && state.getValue(RedstonePortBlock.POWERED)) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/redstone_port/on_in"));
            return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/redstone_port/off_in"));
        });

        horizontalFaceBlock(EGBlocks.ROSE_QUARTZ_PORT.get(), state -> {
            if(state.getValue(RedstonePortBlock.OUTPUT) && state.getValue(RoseQuartzPortBlock.POWER) > 0) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/rose_quartz_port/on_out"));
            if(state.getValue(RedstonePortBlock.OUTPUT) && state.getValue(RoseQuartzPortBlock.POWER) == 0) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/rose_quartz_port/off_out"));
            if(!state.getValue(RedstonePortBlock.OUTPUT) && state.getValue(RoseQuartzPortBlock.POWER) > 0) return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/rose_quartz_port/on_in"));
            return models().getExistingFile(ExtraGauges.CONSTANTS.id("block/rose_quartz_port/off_in"));
        });
    }
}
