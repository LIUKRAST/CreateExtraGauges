package net.liukrast.eg.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.Event;

public class AbstractPanelRenderEvent extends Event {
    public final AbstractPanelBehaviour behaviour;
    public final float partialTicks;
    public final PoseStack poseStack;
    public final MultiBufferSource bufferSource;
    public final int light, overlay;

    public AbstractPanelRenderEvent(AbstractPanelBehaviour behaviour, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        this.behaviour = behaviour;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
        this.overlay = overlay;
    }


}
