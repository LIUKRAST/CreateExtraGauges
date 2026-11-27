package net.liukrast.eg.mixin.withfl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.eg.mixinExtension.PackagerRegexData;
import net.liukrast.eg.mixinExtension.UnwrappedPackageAddress;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

// Create Factory Logistics builds packages in its own createFactoryLogistics$extractBox, merged into PackagerBlockEntity.
// Priority above 1000 is required to inject into a method merged by another mixin.
@Mixin(value = PackagerBlockEntity.class, priority = 1100)
public class PackagerBlockEntityFLSendMixin {
    @Shadow public String signBasedAddress;

    @Inject(method = "createFactoryLogistics$extractBox", remap = false, at = @At("HEAD"))
    private void extra_gauges$forgetUnwrappedAddress(Collection<?> requests, CallbackInfoReturnable<?> cir) {
        UnwrappedPackageAddress.clear();
    }

    @WrapOperation(method = "createFactoryLogistics$extractBox", remap = false, at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;clearAddress(Lnet/minecraft/world/item/ItemStack;)V"))
    private void extra_gauges$computeRewrittenAddress(ItemStack box, Operation<Void> original, @Share("regex_result") LocalRef<String> regexResult) {
        String unwrapped = UnwrappedPackageAddress.take();
        var data = (PackagerRegexData)this;
        regexResult.set(unwrapped != null ? data.extra_gauges$rewriteAddress(unwrapped, signBasedAddress) : data.extra_gauges$rewriteAddress(box, signBasedAddress));
        original.call(box);
    }

    @ModifyExpressionValue(method = "createFactoryLogistics$extractBox", remap = false, at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;signBasedAddress:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String extra_gauges$useRewrittenAddress(String original, @Share("regex_result") LocalRef<String> regexResult) {
        var res = regexResult.get();
        return res == null ? original : res;
    }
}
