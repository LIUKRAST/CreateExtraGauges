package net.liukrast.eg.content.logistics.board;

import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.logistics.board.BasicPanelScreen;
import net.liukrast.eg.networking.StringPanelUpdatePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StringPanelScreen extends BasicPanelScreen<StringPanelBehaviour> {
    public static final ResourceLocation TEXTURE = EGConstants.id("textures/gui/string_gauge.png");

    public static final Component JOIN = Component.translatable("extra_gauges.gui.string_panel.join");
    public static final Component REGEX = Component.translatable("extra_gauges.gui.string_panel.regex");
    public static final Component REPLACEMENT = Component.translatable("extra_gauges.gui.string_panel.replacement");

    private EditBox joinBox,regexBox,replaceBox;
    public StringPanelScreen(StringPanelBehaviour behaviour) {
        super(behaviour);
    }

    @Override
    protected void init() {
        super.init();
        int x = guiLeft;
        int y = guiTop;
        var joinText = joinBox == null ? behaviour.getJoin() : joinBox.getValue();
        joinBox = new EditBox(font, x+32, y+40, 130, 20, Component.empty());
        if(joinText != null) joinBox.setValue(joinText);
        joinBox.setMaxLength(100);
        joinBox.setTextColor(0xFF545454);
        joinBox.setTextShadow(false);
        joinBox.setBordered(false);
        addRenderableWidget(joinBox);
        var regexText = regexBox == null ? behaviour.getRegex() : regexBox.getValue();
        regexBox = new EditBox(font, x+32, y+40+34, 130, 20, Component.empty());
        if(regexText != null) regexBox.setValue(regexText);
        regexBox.setMaxLength(100);
        regexBox.setTextColor(0xFF545454);
        regexBox.setTextShadow(false);
        regexBox.setBordered(false);
        addRenderableWidget(regexBox);
        var replaceText = replaceBox == null ? behaviour.getReplacement() : replaceBox.getValue();
        replaceBox = new EditBox(font,x+32,y+40+34+34,130,30, Component.empty());
        if(replaceText != null) replaceBox.setValue(replaceText);
        replaceBox.setMaxLength(100);
        replaceBox.setTextColor(0xFF545454);
        replaceBox.setTextShadow(false);
        replaceBox.setBordered(false);
        addRenderableWidget(replaceBox);
    }

    @Override
    public int getWindowWidth() {
        return 80;
    }

    @Override
    public int getWindowHeight() {
        return 120;
    }

    @Override
    public void onConfirm() {
        StringPanelUpdatePacket packet = new StringPanelUpdatePacket(behaviour.getPanelPosition(), joinBox.getValue(), regexBox.getValue(), replaceBox.getValue());
        CatnipServices.NETWORK.sendToServer(packet);
        super.onConfirm();
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(font, JOIN, guiLeft+20, guiTop+24, 0xFF442b28, false);
        graphics.blit(TEXTURE, guiLeft+8, guiTop+30, 0,0, 160, 32);
        graphics.drawString(font, REGEX, guiLeft+20, guiTop+24+34, 0xFF442b28, false);
        graphics.blit(TEXTURE, guiLeft+8, guiTop+30+34, 0,0, 160, 32);
        graphics.drawString(font, REPLACEMENT, guiLeft+20, guiTop+24+34+34, 0xFF442b28, false);
        graphics.blit(TEXTURE, guiLeft+8, guiTop+30+34+34, 0,0, 160, 32);
    }
}
