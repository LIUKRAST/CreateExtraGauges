package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.liukrast.eg.content.logistics.LinkedControlBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ValueSettingsInputHandler.class, remap = false)
public class ValueSettingsInputHandlerMixin {
    @ModifyReturnValue(method = "canInteract", at = @At(value = "RETURN"))
    private static boolean canInteract(boolean original, @Local(name = "player") Player player) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Block block = null;

        if (level != null && mc.hitResult instanceof BlockHitResult hitResult) {
            BlockPos pos = hitResult.getBlockPos();
            block = level.getBlockState(pos).getBlock();
        }

        return block instanceof LinkedControlBlock ? createExtraGauges$canInteract(player) : original;
    }

    @Unique
    private static boolean createExtraGauges$canInteract(Player player) {
        return player != null && !player.isSpectator() && !AdventureUtil.isAdventure(player);
    }
}
