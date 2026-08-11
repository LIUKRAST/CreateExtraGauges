package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.liukrast.eg.content.logistics.link.LinkedControlBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ValueSettingsInputHandler.class)
public class ValueSettingsInputHandlerMixin {
    @ModifyExpressionValue(
            method = "onBlockActivated",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsInputHandler;canInteract(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private static boolean canInteract(
            boolean original,
            @Local(name = "world") Level level,
            @Local(name = "pos") BlockPos pos,
            @Local(name = "player") Player player
    ) {
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof LinkedControlBlock
                ? player != null && !player.isSpectator() && !AdventureUtil.isAdventure(player)
                : original;
    }
}
