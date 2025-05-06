package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FactoryPanelConnection.class)
public class FactoryPanelConnectionMixin {

    @Inject(method = "getPath", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock;getXRot(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    private void getPath(Level level, BlockState state, FactoryPanelPosition to, CallbackInfoReturnable<List<Direction>> cir, @Local(ordinal = 1) LocalRef<Vec3> start) {
        if(level.getBlockEntity(to.pos()) instanceof LogicGaugeBlockEntity logicGauge) {
            start.set(logicGauge.behaviour.getSlotPositioning()
                    .getLocalOffset(level, to.pos(), state)
                    .add(Vec3.atLowerCornerOf(to.pos())));
        }
    }
}
