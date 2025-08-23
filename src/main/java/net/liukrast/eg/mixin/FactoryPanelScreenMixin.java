package net.liukrast.eg.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.util.IFPExtra;
import net.liukrast.eg.networking.FactoryPanelChangeSizePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(FactoryPanelScreen.class)
public abstract class FactoryPanelScreenMixin extends AbstractSimiScreen {
    @Unique private static final ResourceLocation extra_gauges$TEXTURE = EGConstants.id("textures/gui/auto_crafting_gauge.png");

    @Unique private MechanicalCraftingRecipe extra_gauges$availableMechanicalRecipe = null;

    @Shadow private CraftingRecipe availableCraftingRecipe;
    @Shadow private List<BigItemStack> inputConfig;

    @Shadow protected abstract void updateConfigs();

    @Shadow private boolean craftingActive;
    @Unique private int extra_gauges$width = 3;
    @Unique private int extra_gauges$height = 3;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private void init(FactoryPanelBehaviour behaviour, CallbackInfo ci) {
        IFPExtra extra = (IFPExtra) behaviour;
        extra_gauges$width = extra.extra_gauges$getWidth();
    }

    @Inject(method = "searchForCraftingRecipe", at = @At("TAIL"))
    private void searchForCraftingRecipe(CallbackInfo ci, @Local ItemStack output, @Local Set<Item> itemsToUse, @Local ClientLevel level) {
        if(availableCraftingRecipe != null) return;
        extra_gauges$availableMechanicalRecipe = level.getRecipeManager()
                .getAllRecipesFor(AllRecipeTypes.MECHANICAL_CRAFTING.<CraftingInput, MechanicalCraftingRecipe>getType())
                .parallelStream()
                .filter(r -> output.getItem() == r.value().getResultItem(level.registryAccess())
                        .getItem()).filter(r -> {
                    if (AllRecipeTypes.shouldIgnoreInAutomation(r))
                        return false;
                    Set<Item> itemsUsed = new HashSet<>();
                    for (Ingredient ingredient : r.value().getIngredients()) {
                        if (ingredient.isEmpty())
                            continue;
                        boolean available = false;
                        for (BigItemStack bis : inputConfig) {
                            if (!bis.stack.isEmpty() && ingredient.test(bis.stack)) {
                                available = true;
                                itemsUsed.add(bis.stack.getItem());
                                break;
                            }
                        }
                        if (!available)
                            return false;
                    }
                    return itemsUsed.size() >= itemsToUse.size();
                })
                .findAny()
                .map(RecipeHolder::value)
                .orElse(null);
    }

    @Definition(id = "availableCraftingRecipe", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelScreen;availableCraftingRecipe:Lnet/minecraft/world/item/crafting/CraftingRecipe;")
    @Expression("this.availableCraftingRecipe != null")
    @ModifyExpressionValue(method = "init", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean init(boolean original) {
        return original || extra_gauges$availableMechanicalRecipe != null;
    }

    @WrapOperation(method = "lambda$init$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CraftingRecipe;getResultItem(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack lambda$init$5(CraftingRecipe instance, HolderLookup.Provider provider, Operation<ItemStack> original) {
        if(extra_gauges$availableMechanicalRecipe != null) return extra_gauges$availableMechanicalRecipe
                .getResultItem(provider);
        return original.call(instance, provider);
    }

    @Definition(id = "availableCraftingRecipe", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelScreen;availableCraftingRecipe:Lnet/minecraft/world/item/crafting/CraftingRecipe;")
    @Expression("this.availableCraftingRecipe == null")
    @ModifyExpressionValue(method = "updateConfigs", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean updateConfigs(boolean original) {
        return original && extra_gauges$availableMechanicalRecipe == null;
    }

    @WrapOperation(method = "updateConfigs", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelScreen;convertRecipeToPackageOrderContext(Lnet/minecraft/world/item/crafting/CraftingRecipe;Ljava/util/List;Z)Ljava/util/List;"))
    private List<BigItemStack> updateConfigs(CraftingRecipe shaped, List<BigItemStack> i, boolean bigItemStack, Operation<List<BigItemStack>> original) {
        List<BigItemStack> list;
        if(extra_gauges$availableMechanicalRecipe != null) {
            if(extra_gauges$width < extra_gauges$availableMechanicalRecipe.getWidth()) extra_gauges$width = extra_gauges$availableMechanicalRecipe.getWidth();
            if(extra_gauges$height < extra_gauges$availableMechanicalRecipe.getHeight()) extra_gauges$height = extra_gauges$availableMechanicalRecipe.getHeight();
            list = extra_gauges$convertMechanicalToPackageOrderContext(extra_gauges$availableMechanicalRecipe, i, bigItemStack);
        } else list = original.call(shaped, i, bigItemStack);
        BigItemStack emptyIngredient = new BigItemStack(ItemStack.EMPTY, 0);
        int k = 0;
        var finalList = ImmutableList.<BigItemStack>builder();
        int w = extra_gauges$availableMechanicalRecipe != null ? extra_gauges$availableMechanicalRecipe.getWidth() : 3;
        int h = extra_gauges$availableMechanicalRecipe != null ? extra_gauges$availableMechanicalRecipe.getHeight() : 3;
        for(BigItemStack stack : list) {
            k++;
            finalList.add(stack);
            if(k%w==0) for(int x = 0; x < extra_gauges$width-w; x++) finalList.add(emptyIngredient);
        }
        for(int x = 0; x < extra_gauges$height-h; x++) finalList.add(emptyIngredient);
        return finalList.build();
    }

    @Unique
    private static List<BigItemStack> extra_gauges$convertMechanicalToPackageOrderContext(MechanicalCraftingRecipe recipe, List<BigItemStack> inputs, boolean respectAmounts) {
        List<BigItemStack> craftingIngredients = new ArrayList<>();
        BigItemStack emptyIngredient = new BigItemStack(ItemStack.EMPTY, 1);
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<BigItemStack> mutableInputs = BigItemStack.duplicateWrappers(inputs);

        int width = recipe.getWidth();
        int height = recipe.getHeight();

        if (height == 1)
            for (int i = 0; i < 3; i++)
                craftingIngredients.add(emptyIngredient);
        if (width == 1)
            craftingIngredients.add(emptyIngredient);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            BigItemStack craftingIngredient = emptyIngredient;

            if (!ingredient.isEmpty())
                for (BigItemStack bigItemStack : mutableInputs)
                    if (bigItemStack.count > 0 && ingredient.test(bigItemStack.stack)) {
                        craftingIngredient = new BigItemStack(bigItemStack.stack, 1);
                        if (respectAmounts)
                            bigItemStack.count -= 1;
                        break;
                    }

            craftingIngredients.add(craftingIngredient);

            if (width < 3 && (i + 1) % width == 0)
                for (int j = 0; j < 3 - width; j++)
                    if (craftingIngredients.size() < 9)
                        craftingIngredients.add(emptyIngredient);
        }

        while (craftingIngredients.size() < 9)
            craftingIngredients.add(emptyIngredient);

        return craftingIngredients;
    }

    @Definition(id = "slot", local = @Local(type = int.class, ordinal = 4))
    @Expression("slot = 0")
    @Inject(method = "renderWindow", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci, @Local(ordinal = 2) int x, @Local(ordinal = 3) int y) {
        if(!craftingActive) return;
        if(extra_gauges$availableMechanicalRecipe != null) {
            graphics.blit(extra_gauges$TEXTURE, x + 56, y + 23, 0, 0, 79, 72);
            if (!(mouseX < x + 56 + 11 || mouseX > x + 56 + 68 || mouseY < y + 23 + 4 || mouseY > y + 23 + 61)) {
                graphics.renderComponentTooltip(font, List.of(
                        Component.translatable("extra_gauges.gui.factory_panel.auto_crafting_input")
                                .withStyle(style -> style.withColor(0xFFeeda78)),
                        CreateLang.translate("gui.factory_panel.crafting_input_tip")
                                .style(ChatFormatting.GRAY)
                                .component(),
                        Component.translatable("extra_gauges.gui.factory_panel.crafting_input_tip_1", extra_gauges$width)
                                .withStyle(ChatFormatting.GRAY),
                        Component.translatable("extra_gauges.gui.factory_panel.crafting_input_tip_2", extra_gauges$height)
                                .withStyle(ChatFormatting.GRAY)
                ), mouseX, mouseY);
            }
        }
        extra_gauges$generateButton(graphics,x+56+75, y+23+14, 80, mouseX, mouseY, Component.translatable("extra_gauges.gui.factory_panel.extend_width", extra_gauges$width));
        if(extra_gauges$width > (extra_gauges$availableMechanicalRecipe == null ? 3 : extra_gauges$availableMechanicalRecipe.getWidth())) extra_gauges$generateButton(graphics, x+56+75, y+23+14+22, 88, mouseX, mouseY, Component.translatable("extra_gauges.gui.factory_panel.reduce_width", extra_gauges$width));
    }

    @ModifyExpressionValue(method = "renderWindow", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelScreen;craftingIngredients:Ljava/util/List;"))
    private List<BigItemStack> renderWindow(List<BigItemStack> original) {
        if(extra_gauges$width==0) return original;
        int k = 0;
        List<BigItemStack> out = new ArrayList<>();
        for(BigItemStack v : original) {
            if(k%extra_gauges$width<3) out.add(v);
            k++;
        }
        return out;
    }

    @WrapWithCondition(method = "renderWindow", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelScreen;renderInputItem(Lnet/minecraft/client/gui/GuiGraphics;ILcom/simibubi/create/content/logistics/BigItemStack;II)V", ordinal = 0))
    private boolean renderWindow(FactoryPanelScreen instance, GuiGraphics graphics, int slot, BigItemStack itemStack, int mouseX, int mouseY, @Local(ordinal = 4) LocalIntRef slotRef) {
        return extra_gauges$availableMechanicalRecipe == null;
    }

    @Unique
    private void extra_gauges$generateButton(GuiGraphics guiGraphics, int x, int y, int tx, int mouseX, int mouseY, Component tooltip) {
        boolean hovered = mouseX > x && mouseX < x+ 8 && mouseY > y && mouseY < y+ 16;
        guiGraphics.blit(extra_gauges$TEXTURE, x, y, tx, (hovered ? 16 : 0), 8, 16);
        if(hovered) guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
    }

    @ModifyExpressionValue(method = "renderInputItem", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/lang/LangBuilder;component()Lnet/minecraft/network/chat/MutableComponent;", ordinal = 2))
    private MutableComponent renderInputItem(MutableComponent original) {
        return Component.translatable("extra_gauges.gui.factory_panel.crafting_input_tip_1", extra_gauges$width)
                .withStyle(ChatFormatting.GRAY);
    }

    @ModifyExpressionValue(method = "renderInputItem", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;", ordinal = 0))
    private List<MutableComponent> renderInputItem(List<MutableComponent> original) {
        var builder = ImmutableList.<MutableComponent>builder();
        builder.addAll(original);
        builder.add(Component.translatable("extra_gauges.gui.factory_panel.crafting_input_tip_2", extra_gauges$height)
                .withStyle(ChatFormatting.GRAY));
        return builder.build();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if(!craftingActive) return;
        if(mouseX > guiLeft+56+75 && mouseX < guiLeft+56+75+8 && mouseY > guiTop+23+14 && mouseY < guiTop+23+14+16) {
            extra_gauges$width++;
            updateConfigs();
            cir.setReturnValue(true);
            cir.cancel();
        }
        if(extra_gauges$width > (extra_gauges$availableMechanicalRecipe == null ? 3 : extra_gauges$availableMechanicalRecipe.getWidth()) && mouseX > guiLeft+56+75 && mouseX < guiLeft+56+75+8 && mouseY > guiTop+23+14+22 && mouseY < guiTop+23+14+22+16) {
            extra_gauges$width--;
            updateConfigs();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "sendIt", at = @At("TAIL"))
    private void sendIt(FactoryPanelPosition toRemove, boolean clearPromises, CallbackInfo ci, @Local(ordinal = 1) FactoryPanelPosition pos) {
        FactoryPanelChangeSizePacket packet = new FactoryPanelChangeSizePacket(pos, extra_gauges$width);
        CatnipServices.NETWORK.sendToServer(packet);
    }
}
