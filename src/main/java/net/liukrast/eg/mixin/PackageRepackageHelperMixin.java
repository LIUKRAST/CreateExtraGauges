package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.repackager.PackageRepackageHelper;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import net.liukrast.eg.TEST;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PackageRepackageHelper.class)
public class PackageRepackageHelperMixin {

    @Inject(method = "repackBasedOnRecipes", at = @At("HEAD"), cancellable = true)
    private void repackBasedOnRecipes(InventorySummary summary, PackageOrderWithCrafts order, String address, RandomSource r, CallbackInfoReturnable<List<BigItemStack>> cir) {
        cir.setReturnValue(TEST.repackBasedOnRecipes(summary, order, address, r));
        cir.cancel();
    }
}
