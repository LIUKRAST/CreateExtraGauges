package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import net.createmod.catnip.math.AngleHelper;
import net.liukrast.eg.content.block.logic.LogicGaugeBlock;
import net.liukrast.eg.registry.RegisterBlocks;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FactoryPanelBlock.class)
public class FactoryPanelBlockMixin {

    @Inject(method = "getXRot", at = @At("RETURN"), cancellable = true)
    private static void getXRot(BlockState state, CallbackInfoReturnable<Float> cir) {
        if(state.is(RegisterBlocks.LOGIC_GAUGE)) {
            var facing = state.getValue(LogicGaugeBlock.FACING);
            cir.setReturnValue((facing == Direction.UP ? -Mth.PI / 2 : facing == Direction.DOWN ? Mth.PI / 2 : 0));
        }
    }

    @Inject(method = "getYRot", at = @At("RETURN"), cancellable = true)
    private static void getYRot(BlockState state, CallbackInfoReturnable<Float> cir) {
        if(state.is(RegisterBlocks.LOGIC_GAUGE)) {
            var facing = state.getValue(LogicGaugeBlock.FACING);
            cir.setReturnValue((facing == Direction.DOWN ? Mth.PI : 0) + AngleHelper.rad(AngleHelper.horizontalAngle(facing)));
        }
    }
}
