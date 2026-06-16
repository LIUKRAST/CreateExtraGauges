package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.LinkHandler;
import net.liukrast.eg.content.logistics.link.LinkedControlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LinkHandler.class)
public class LinkHandlerMixin {
    @ModifyExpressionValue(
            method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isShiftKeyDown()Z")
    )
    private static boolean shiftKeyCondition(boolean original,
                                             @Local(name = "world") Level world,
                                             @Local(name = "pos") BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof LinkedControlBlock) return !original;
        return original;
    }
}
