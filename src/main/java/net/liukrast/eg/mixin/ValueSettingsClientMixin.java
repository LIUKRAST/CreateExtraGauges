package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(value = ValueSettingsClient.class, remap = false)
public class ValueSettingsClientMixin {

    @WrapOperation(method = "tick", at = @At(value = "NEW", target = "(Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsBoard;Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsBehaviour$ValueSettings;Ljava/util/function/Consumer;I)Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsScreen;"))
    private ValueSettingsScreen tick(BlockPos pos, ValueSettingsBoard board, ValueSettingsBehaviour.ValueSettings valueSettings, Consumer<ValueSettingsBehaviour.ValueSettings> onHover, int netId, Operation<ValueSettingsScreen> original) {
        if(board == null) return null;
        return original.call(pos, board, valueSettings, onHover, netId);
    }
}
