package net.liukrast.eg.content.logistics.board;

import net.liukrast.eg.ExtraGauges;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FunctionIndexScreen extends Screen {

    

    public static final ResourceLocation TEXTURE = ExtraGauges.CONSTANTS.id("textures/gui/function_index.png");

    public FunctionIndexScreen(Component title, ExpressionPanelScreen parent) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int w = 206;
        int h = 236;
        graphics.blit(TEXTURE, (width-w)>>1,(height-h)>>1, 24, 19, 206, 236);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
