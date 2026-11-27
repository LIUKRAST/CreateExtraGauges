package net.liukrast.eg.mixin.withoutfl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import net.liukrast.eg.mixinExtension.PackagerRegexData;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PackagerBlockEntity.class)
public class PackagerBlockEntitySendMixin {
    @Shadow public String signBasedAddress;

    @Inject(method = "attemptToSend", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;clearAddress(Lnet/minecraft/world/item/ItemStack;)V"))
    private void extra_gauges$computeRewrittenAddress(
            List<PackagingRequest> queuedRequests,
            CallbackInfo ci,
            @Share("regex_result") LocalRef<String> regexResult,
            @Local(name = "createdBox") ItemStack createdBox
    ) {
        regexResult.set(((PackagerRegexData)this).extra_gauges$rewriteAddress(createdBox, signBasedAddress));
    }

    @ModifyExpressionValue(method = "attemptToSend", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;signBasedAddress:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String extra_gauges$useRewrittenAddress(String original, @Share("regex_result") LocalRef<String> regexResult) {
        var res = regexResult.get();
        return res == null ? original : res;
    }
}
