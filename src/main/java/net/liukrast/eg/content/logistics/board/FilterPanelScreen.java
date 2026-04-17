package net.liukrast.eg.content.logistics.board;

import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.logistics.board.screen.BasicPanelScreen;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.networking.FilterPanelEjectPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FilterPanelScreen extends BasicPanelScreen<FilterPanelBehaviour> {
    private static final ResourceLocation TEXTURE = EGConstants.id("textures/gui/filter_gauge.png");

    public FilterPanelScreen(FilterPanelBehaviour behaviour) {
        super(behaviour, false, true);
    }

    @Override
    public int getWindowHeight() {
        return 40;
    }

    @Override
    public int getWindowWidth() {
        return super.getWindowWidth();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= guiLeft+41 && mouseX <= guiLeft+41+24 && mouseY >= guiTop+30 && mouseY <= guiTop+30+19) {
            CatnipServices.NETWORK.sendToServer(new FilterPanelEjectPacket(behaviour.getPanelPosition()));
            assert minecraft != null;
            minecraft.setScreen(null);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        boolean hovered = mouseX >= guiLeft+41 && mouseX <= guiLeft+41+24 && mouseY >= guiTop+30 && mouseY <= guiTop+30+19;
        graphics.blit(TEXTURE, guiLeft + 41, guiTop + 30, hovered ? 24 : 0,0, 24, 19);
        var comp = Component.translatable("extra_gauges.gui.filter_panel.eject");
        int w = font.width(comp);
        graphics.drawString(font, comp, guiLeft + 53 - w/2, guiTop + 20, 0xFF442b28, false);
    }
}
