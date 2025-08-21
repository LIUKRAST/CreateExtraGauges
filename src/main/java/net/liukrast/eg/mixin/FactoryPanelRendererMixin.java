package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.factoryBoard.*;
import net.liukrast.eg.api.event.AbstractPanelRenderEvent;
import net.liukrast.eg.api.logistics.ColoredFactoryPanelSupportBehaviour;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.util.IFPExtra;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FactoryPanelRenderer.class)
public class FactoryPanelRendererMixin {

    /* Allows abstract panels to have their own render system + decides whether a bulb should be rendered or not */
    @ModifyExpressionValue(
            method = "renderSafe(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;getAmount()I")
    )
    private int renderSafe(int original, @Local FactoryPanelBehaviour behaviour, @Local(argsOnly = true) float partialTicks, @Local(argsOnly = true)PoseStack ms, @Local(argsOnly = true)MultiBufferSource buffer, @Local(argsOnly = true, ordinal = 0) int light, @Local(argsOnly = true, ordinal = 1) int overlay) {
        if (behaviour instanceof AbstractPanelBehaviour abstractPanel) {
            ms.pushPose();
            NeoForge.EVENT_BUS.post(new AbstractPanelRenderEvent(abstractPanel, partialTicks, ms, buffer, light, overlay));
            ms.popPose();
            return abstractPanel.shouldRenderBulb(original > 0) ? 1 : 0;
        } else return original;
    }

    /* Render extra connections */
    @Inject(
            method = "renderSafe(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0)
    )
    private void renderSafe(FactoryPanelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci, @Local FactoryPanelBehaviour behaviour) {
        for(FactoryPanelConnection connection : ((IFPExtra)behaviour).extra_gauges$getExtra().values())
            FactoryPanelRenderer.renderPath(behaviour, connection, partialTicks, ms, buffer, light, overlay);
    }

    /* Render paths */
    @ModifyArg(method = "renderPath", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;color(I)Lnet/createmod/catnip/render/SuperByteBuffer;"))
    private static int renderPath(int color, @Local(argsOnly = true) FactoryPanelBehaviour behaviour, @Local(argsOnly = true) FactoryPanelConnection connection, @Local FactoryPanelSupportBehaviour support, @Local(ordinal = 1) boolean redstoneLinkMode, @Local(ordinal = 4) LocalBooleanRef dots) {
        if(redstoneLinkMode && behaviour instanceof AbstractPanelBehaviour ab) {
            var opt = ab.getConnectionValue(EGPanelConnections.REDSTONE.get());
            if(opt.isPresent()) {
                return opt.orElse(0)==0?0x580101:0xEF0000;
            }
        }
        if(support instanceof ColoredFactoryPanelSupportBehaviour colored) {
            var line = colored.getColor(behaviour);
            dots.set(line.dots());
            return line.color();
        }
        if(support != null) return color;
        var other = FactoryPanelBehaviour.at(behaviour.getWorld(), connection);
        if(behaviour instanceof AbstractPanelBehaviour ab) {
            return other != null ? ab.calculatePath(other, color) : ab.calculateExtraPath(connection.from.pos());
        } else if(other instanceof AbstractPanelBehaviour ab) {
            for(PanelConnection<?> c : ab.getConnections()) {
                if(c == EGPanelConnections.FILTER.get()) return color;
                if(c == EGPanelConnections.INTEGER.get()) return 0x006496;
                if(c == EGPanelConnections.REDSTONE.get()) return EGPanelConnections.getConnectionValue(ab, EGPanelConnections.REDSTONE).orElse(0)==0?0x580101:0xEF0000;
                if(c == EGPanelConnections.STRING.get()) return 0xFFFFFFFF;
            }
            return 0x888898;
        }
        return color;
    }
}
