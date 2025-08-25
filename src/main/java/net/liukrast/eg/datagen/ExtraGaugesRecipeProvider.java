package net.liukrast.eg.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.registry.EGBlocks;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ExtraGaugesRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ExtraGaugesRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        makeGauge(EGItems.LOGIC_GAUGE.get(), Items.REDSTONE_TORCH, writer);
        makeGauge(EGItems.INT_GAUGE.get(), Items.QUARTZ, writer);
        makeGauge(EGItems.COMPARATOR_GAUGE.get(), Items.COMPARATOR, writer);
        makeGauge(EGItems.COUNTER_GAUGE.get(), AllItems.TRANSMITTER.get(), writer);
        makeGauge(EGItems.PASSIVE_GAUGE.get(), AllItems.STURDY_SHEET.get(), writer);
        makeGauge(EGItems.STRING_GAUGE.get(), Items.PAPER, writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, EGBlocks.DISPLAY_COLLECTOR.get())
                .requires(AllBlocks.DISPLAY_LINK)
                .unlockedBy("has_display_link", has(AllBlocks.DISPLAY_LINK)).save(writer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, AllBlocks.DISPLAY_LINK.get())
                .requires(EGBlocks.DISPLAY_COLLECTOR.get())
                .unlockedBy("has_display_collector", has(EGBlocks.DISPLAY_COLLECTOR.get())).save(writer, EGConstants.id("display_link"));
    }

    public void makeGauge(Item result, Item ingredient, @NotNull Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, result)
                .requires(AllItems.PRECISION_MECHANISM)
                .requires(ingredient)
                .unlockedBy("has_precision_mechanism", has(AllItems.PRECISION_MECHANISM)).save(writer);
    }
}
