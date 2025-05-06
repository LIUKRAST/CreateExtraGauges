package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FactoryPanelConnectionHandler.class)
public class FactoryPanelConnectionHandlerMixin {

    @Shadow
    static FactoryPanelPosition connectingFrom;

    @Definition(id = "at", local = @Local(type = FactoryPanelBehaviour.class))
    @Expression("at == null")
    @ModifyExpressionValue(method = "clientTick", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static boolean clientTick(boolean original, @Local Minecraft mc) {
        return original && !(mc.level.getBlockEntity(connectingFrom.pos()) instanceof LogicGaugeBlockEntity);
    }

    @ModifyVariable(method = "onRightClick", at = @At(value = "STORE", ordinal = 0))
    private static String onRightClick(String value, @Local Minecraft mc) {
        if(!(mc.level.getBlockEntity(connectingFrom.pos()) instanceof LogicGaugeBlockEntity)) return value;
        return null;
    }


}
