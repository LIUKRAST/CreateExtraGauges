package net.liukrast.eg.datagen;

import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ExtraGaugesLootTableProvider extends BlockLootSubProvider {
    public ExtraGaugesLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        EGBlocks.BLOCKS.getEntries().stream().map(Holder::value).forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return EGBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
