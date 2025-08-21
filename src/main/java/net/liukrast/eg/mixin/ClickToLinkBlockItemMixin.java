package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem;
import net.liukrast.eg.content.item.DisplayCollectorBlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClickToLinkBlockItem.class)
public class ClickToLinkBlockItemMixin {
    @ModifyArg(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/outliner/Outline$OutlineParams;colored(I)Lnet/createmod/catnip/outliner/Outline$OutlineParams;"))
    private static int clientTick(int color, @Local ClickToLinkBlockItem blockItem) {
        return blockItem instanceof DisplayCollectorBlockItem ? 0x7FCDE0 : color;
    }
}
