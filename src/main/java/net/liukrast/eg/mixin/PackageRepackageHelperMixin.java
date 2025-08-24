package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.repackager.PackageRepackageHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = PackageRepackageHelper.class, remap = false)
public class PackageRepackageHelperMixin {


    @ModifyExpressionValue(method = "repackBasedOnRecipes", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/stockTicker/PackageOrder;stacks()Ljava/util/List;", ordinal = 1))
    private List<BigItemStack> repackBasedOnRecipes(List<BigItemStack> original) {
        List<ItemStack> copied = original.stream().map(big -> big.stack.copy()).toList();
        List<ItemStack> result = NonNullList.withSize(copied.size(), ItemStack.EMPTY);
        for(var stack : copied) {
            for(int i = 0; i < result.size(); i++) {
                var slot = result.get(i);
                if(slot.isEmpty()) {
                    result.set(i, stack);
                    break;
                } else if(ItemStack.isSameItemSameTags(stack, slot)) {
                    int canPut = slot.getMaxStackSize() - slot.getCount();
                    slot.setCount(slot.getCount() + Mth.clamp(stack.getCount(),0,canPut));
                    stack.setCount(Math.max(stack.getCount()-canPut, 0));
                    if(stack.getCount() == 0) break;
                }
            }
        }
        return result.stream().map(BigItemStack::new).toList();
    }

    @WrapOperation(method = "repackBasedOnRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private ItemStack repackBasedOnRecipes(ItemStack instance, int i, Operation<ItemStack> original) {
        return instance.copy();
    }
}
