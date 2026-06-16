package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import net.liukrast.eg.content.logistics.link.LinkedButtonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Contraption.class)
public class ContraptionMixin {
    @ModifyArg(method = "capture", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;)V"))
    private BlockState capture(BlockState blockState,
                               @Local(name = "world") Level world,
                               @Local(name = "pos") BlockPos pos) {
        if (blockState.getBlock() instanceof LinkedButtonBlock) {
            blockState = blockState.setValue(LinkedButtonBlock.POWERED, false);
            world.scheduleTick(pos, blockState.getBlock(), -1);
        }
        return blockState;
    }
}
