package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FilteringRenderer.class)
public class FilteringRendererMixin {

    @Definition(id = "b", local = @Local(type = BlockEntityBehaviour.class))
    @Definition(id = "FilteringBehaviour", type = FilteringBehaviour.class)
    @Expression("b instanceof FilteringBehaviour")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tick(boolean original, @Local BlockEntityBehaviour b) {
        return b instanceof AbstractPanelBehaviour panel ? panel.withFilteringBehaviour() : original;
    }
}
