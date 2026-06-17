package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import net.liukrast.eg.content.logistics.LinkedControlBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LinkRenderer.class, remap = false)
public class LinkRendererMixin {
    @ModifyArg(
            method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/CreateLang;translateDirect(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 2)
    )
    private static String modifyText(String key,
                                     @Local(name = "world") ClientLevel world,
                                     @Local(name = "pos") BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof LinkedControlBlock) return key.replace("click", "shift_click");
        return key;
    }
}
