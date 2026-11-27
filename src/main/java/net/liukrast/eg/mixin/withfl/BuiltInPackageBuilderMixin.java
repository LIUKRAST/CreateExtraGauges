package net.liukrast.eg.mixin.withfl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.eg.mixinExtension.UnwrappedPackageAddress;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Create Factory Logistics opens packages pulled by a packager here and builds a new one, so this is the last place the original address is known
@Mixin(targets = "ru.zznty.create_factory_abstractions.generic.impl.BuiltInPackageBuilder", remap = false)
public class BuiltInPackageBuilderMixin {
    @WrapOperation(method = "add", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;getContents(Lnet/minecraft/world/item/ItemStack;)Lnet/neoforged/neoforge/items/ItemStackHandler;"))
    private ItemStackHandler extra_gauges$recordAddress(ItemStack box, Operation<ItemStackHandler> original) {
        UnwrappedPackageAddress.record(PackageItem.getAddress(box));
        return original.call(box);
    }
}
