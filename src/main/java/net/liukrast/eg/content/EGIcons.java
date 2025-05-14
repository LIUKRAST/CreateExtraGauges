package net.liukrast.eg.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.theme.Color;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.mixin.AllIconsMixin;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

public class EGIcons extends AllIcons {
    public static final ResourceLocation ICON_ATLAS = ExtraGauges.id("textures/gui/icons.png");
    public static final int ICON_ATLAS_SIZE = 64;

    private static int x = 0, y = -1;

    public static final EGIcons
            I_OR = newRow(),
            I_AND = next(),
            I_NAND = next(),
            I_NOR = next(),

            I_XOR = newRow(),
            I_XNOR = next(),
            I_SUBTRACT = next(),
            I_MULTIPLY = next(),

            I_EQUALS = newRow(),
            I_DIFFERENT = next(),
            I_GREATER = next(),
            I_GREATER_EQUALS = next(),

            I_LESS = newRow(),
            I_LESS_EQUALS = next();

    private static EGIcons next() {
        return new EGIcons(++x, y);
    }

    private static EGIcons newRow() {
        return new EGIcons(x = 0, ++y);
    }

    private EGIcons(int x, int y) {
        super(x, y);
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, ICON_ATLAS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICON_ATLAS, x, y, 0, ((AllIconsMixin)this).getIconX(), ((AllIconsMixin)this).getIconY(), 16, 16, ICON_ATLAS_SIZE, ICON_ATLAS_SIZE);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text(ICON_ATLAS));
        Matrix4f matrix = ms.last().pose();
        Color rgb = new Color(color);
        int light = LightTexture.FULL_BRIGHT;

        Vec3 vec1 = new Vec3(0, 0, 0);
        Vec3 vec2 = new Vec3(0, 1, 0);
        Vec3 vec3 = new Vec3(1, 1, 0);
        Vec3 vec4 = new Vec3(1, 0, 0);
        var iconX = ((AllIconsMixin)this).getIconX();
        var iconY = ((AllIconsMixin)this).getIconY();

        float u1 = iconX * 1f / ICON_ATLAS_SIZE;
        float u2 = (iconX + 16) * 1f / ICON_ATLAS_SIZE;
        float v1 = iconY * 1f / ICON_ATLAS_SIZE;
        float v2 = (iconY + 16) * 1f / ICON_ATLAS_SIZE;

        var cast = (AllIconsMixin)this;

        cast.invokeVertex(builder, matrix, vec1, rgb, u1, v1, light);
        cast.invokeVertex(builder, matrix, vec2, rgb, u1, v2, light);
        cast.invokeVertex(builder, matrix, vec3, rgb, u2, v2, light);
        cast.invokeVertex(builder, matrix, vec4, rgb, u2, v1, light);
    }
}