package net.liukrast.eg.datagen;

import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ExtraGaugesLootTableProvider extends BlockLootSubProvider {
    public ExtraGaugesLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        EGBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return EGBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
