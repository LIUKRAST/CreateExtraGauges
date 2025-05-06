package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelRenderer;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FactoryPanelRenderer.class)
public class FactoryPanelRendererMixin {
    @ModifyVariable(method = "renderPath", at = @At("STORE"), ordinal = 1)
    private static boolean renderPath(boolean original, @Local FactoryPanelSupportBehaviour sbe) {
        return original || (sbe != null && sbe.blockEntity instanceof LogicGaugeBlockEntity);
    }
}
