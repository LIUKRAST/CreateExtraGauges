package net.liukrast.eg.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.registry.EGBlocks;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ExtraGaugesRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ExtraGaugesRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        makeGauge(EGItems.LOGIC_GAUGE.get(), Items.REDSTONE_TORCH, output);
        makeGauge(EGItems.INT_GAUGE.get(), Items.QUARTZ, output);
        makeGauge(EGItems.COMPARATOR_GAUGE.get(), Items.COMPARATOR, output);
        makeGauge(EGItems.COUNTER_GAUGE.get(), AllItems.TRANSMITTER.get(), output);
        makeGauge(EGItems.PASSIVE_GAUGE.get(), AllItems.STURDY_SHEET.get(), output);
        makeGauge(EGItems.STRING_GAUGE.get(), Items.PAPER, output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, EGBlocks.INT_SELECTOR.get())
                .requires(AllBlocks.ANALOG_LEVER.get())
                .requires(Items.QUARTZ)
                .unlockedBy("has_analog_lever", has(AllBlocks.ANALOG_LEVER)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, EGBlocks.DISPLAY_COLLECTOR.get())
                .requires(AllBlocks.DISPLAY_LINK)
                .unlockedBy("has_display_link", has(AllBlocks.DISPLAY_LINK)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, AllBlocks.DISPLAY_LINK.get())
                .requires(EGBlocks.DISPLAY_COLLECTOR.get())
                .unlockedBy("has_display_collector", has(EGBlocks.DISPLAY_COLLECTOR.get())).save(output, EGConstants.id("display_link"));
    }

    public void makeGauge(Item result, Item ingredient, @NotNull RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, result)
                .requires(AllItems.PRECISION_MECHANISM)
                .requires(ingredient)
                .unlockedBy("has_precision_mechanism", has(AllItems.PRECISION_MECHANISM)).save(output);
    }
}
