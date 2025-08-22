package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.repackager.PackageRepackageHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PackageRepackageHelper.class)
public class PackageRepackageHelperMixin {

    @ModifyExpressionValue(method = "repackBasedOnRecipes", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/stockTicker/PackageOrder;stacks()Ljava/util/List;", ordinal = 1))
    private List<BigItemStack> repackBasedOnRecipes(List<BigItemStack> original, @Local(argsOnly = true) InventorySummary summary) {
        return summary.getStacks();
    }
}
