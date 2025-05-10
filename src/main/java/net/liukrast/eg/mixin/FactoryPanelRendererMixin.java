package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelRenderer;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export = true)
@Mixin(FactoryPanelRenderer.class)
public class FactoryPanelRendererMixin {

    @ModifyExpressionValue(
            method = "renderSafe(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;getAmount()I"))
    private int renderSafe(int original, @Local FactoryPanelBehaviour behaviour) {
        return behaviour instanceof AbstractPanelBehaviour abstractPanel ? abstractPanel.shouldRenderBulb() ? 1 : 0 : original;
    }

    @ModifyArg(method = "renderPath", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;color(I)Lnet/createmod/catnip/render/SuperByteBuffer;"))
    private static int renderPath(int color, @Local(argsOnly = true) FactoryPanelBehaviour behaviour, @Local(argsOnly = true) FactoryPanelConnection connection, @Local(ordinal = 0) boolean displayLinkMode, @Local(ordinal = 1) boolean redstoneLinkMode) {
        if(displayLinkMode || redstoneLinkMode) return color;
        var other = FactoryPanelBehaviour.at(behaviour.getWorld(), connection);
        if(other == null) return color;
        if(!(other instanceof AbstractPanelBehaviour) && !(behaviour instanceof AbstractPanelBehaviour)) return color;
        var connectionsA = PanelConnections.getConnections(behaviour);
        var connectionsB = PanelConnections.getConnections(other);
        for(PanelConnection<?> panelConnection : connectionsA.keySet()) {
            if(connectionsB.containsKey(panelConnection)) {
                if(panelConnection == PanelConnections.FILTER) return color;
                return panelConnection.getColorGeneric(connectionsA.get(panelConnection), connectionsB.get(panelConnection));
            }
        }
        return color;
    }

    @Definition(id = "behaviour", local = @Local(type = FactoryPanelBehaviour.class, argsOnly = true))
    @Definition(id = "count", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;count:I")
    @Expression("behaviour.count == 0")
    @ModifyExpressionValue(method = "renderPath", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private static boolean renderPath(boolean original, @Local(argsOnly = true) FactoryPanelBehaviour behaviour) {
        if(behaviour instanceof AbstractPanelBehaviour ab) {
            if(ab.hasConnection(PanelConnections.FILTER)) {
                if(ab.hasConnection(PanelConnections.REDSTONE)) {
                    return !ab.shouldUseRedstoneInsteadOfFilter() && original;
                } else return original;
            } return !ab.hasConnection(PanelConnections.REDSTONE);
        }
        return original;
    }

    @Definition(id = "behaviour", local = @Local(type = FactoryPanelBehaviour.class, argsOnly = true))
    @Definition(id = "satisfied", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;satisfied:Z")
    @Expression("behaviour.satisfied")
    @ModifyExpressionValue(method = "renderPath", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private static boolean renderPath$1(boolean original, @Local(argsOnly = true) FactoryPanelBehaviour behaviour) {
        if(behaviour instanceof AbstractPanelBehaviour ab) {
            return ab.getConnectionValue(PanelConnections.REDSTONE).orElse(false);
        }
        return original;
    }

    @Definition(id = "behaviour", local = @Local(type = FactoryPanelBehaviour.class, argsOnly = true))
    @Definition(id = "redstonePowered", field = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;redstonePowered:Z")
    @Expression("behaviour.redstonePowered")
    @ModifyExpressionValue(method = "renderPath", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private static boolean renderPath$2(boolean original, @Local(argsOnly = true) FactoryPanelBehaviour behaviour, @Local FactoryPanelSupportBehaviour sbe) {
        if(behaviour instanceof AbstractPanelBehaviour ab && ab.hasConnection(PanelConnections.REDSTONE)) {
            return ((RedstoneLinkBlockEntity)sbe.blockEntity).getReceivedSignal() > 0;
        }
        return original;
    }
}
