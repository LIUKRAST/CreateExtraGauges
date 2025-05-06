package net.liukrast.eg.content.block.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.RegisterBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LogicGaugeScreen extends AbstractSimiScreen {
    private static final ResourceLocation TEXTURE = ExtraGauges.id("textures/gui/logic_gauge.png");

    private IconButton confirmButton;
    private IconButton deleteButton;
    private IconButton newInputButton;
    private IconButton newOutputButton;
    //TODO: private IconButton relocateButton;
    private LogicGaugeBehaviour behaviour;

    public LogicGaugeScreen(LogicGaugeBehaviour behaviour) {
        this.behaviour = behaviour;
        minecraft = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        int sizeX = 200;
        int sizeY = 160;
        setWindowSize(sizeX, sizeY);
        super.init();
        clearWidgets();
        int x = guiLeft;
        int y = guiTop;

        confirmButton = new IconButton(x + sizeX - 33, y + sizeY - 25, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.setScreen(null));
        confirmButton.setToolTip(CreateLang.translate("gui.logic_gauge.save_and_close").component());
        addRenderableWidget(confirmButton);

        deleteButton = new IconButton(x + sizeX - 55, y + sizeY - 25, AllIcons.I_TRASH);
        deleteButton.withCallback(() -> {
            //TODO: sendReset = true;
            minecraft.setScreen(null);
        });
        deleteButton.setToolTip(CreateLang.translate("gui.logic_gauge.reset")
                .component());
        addRenderableWidget(deleteButton);

        newInputButton = new IconButton(x + 31, y + 47, AllIcons.I_ADD);
        newInputButton.withCallback(() -> {
            //TODO: FactoryPanelConnectionHandler.startConnection(behaviour);
            minecraft.setScreen(null);
        });
        newInputButton.setToolTip(CreateLang.translate("gui.logic_gauge.connect_input")
                .component());
        addRenderableWidget(newInputButton);

        newOutputButton = new IconButton(x + windowWidth - 57, y + 47, AllIcons.I_ADD);
        newOutputButton.withCallback(() -> {
            LogicGaugeConnectionHandler.startConnection(behaviour);
            minecraft.setScreen(null);
        });
        newOutputButton.setToolTip(CreateLang.translate("gui.logic_gauge.connect_output")
                .component());
        addRenderableWidget(newOutputButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        PoseStack ms = graphics.pose();
        graphics.blit(TEXTURE, x, y, 32, 0, windowWidth, windowHeight);
        var title = Component.translatable("create.gui.logic_gauge.title");
        graphics.drawString(font, title, x + 97 - font.width(title) / 2, y + 4, 0x3D3C48, false);

        behaviour.get().getIcon().render(graphics, x + windowWidth/2 - 10, y+windowHeight/2 - 32);

        // ITEM PREVIEW
        ms.pushPose();
        ms.translate(0, 60, 0);
        GuiGameElement.of(RegisterBlocks.LOGIC_GAUGE.get().asItem().getDefaultInstance())
                .scale(4)
                .at(0, 0, -200)
                .render(graphics, x + 195, y + 55);
        ms.popPose();
    }
}
