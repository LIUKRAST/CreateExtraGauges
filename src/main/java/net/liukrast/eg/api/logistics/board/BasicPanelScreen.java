package net.liukrast.eg.api.logistics.board;

import com.google.common.collect.Lists;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConfigurationPacket;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.liukrast.eg.ExtraGauges;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BasicPanelScreen extends AbstractSimiScreen {
    public static final ResourceLocation TEXTURE = ExtraGauges.id("textures/gui/generic_gauge.png");

    public final AbstractPanelBehaviour behaviour;
    private boolean sendReset;

    public BasicPanelScreen(Component component, AbstractPanelBehaviour behaviour) {
        super(component);
        this.behaviour = behaviour;
    }

    public BasicPanelScreen(AbstractPanelBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public int getWindowWidth() {
        return 112;
    }

    public int getWindowHeight() {
        return 32;
    }

    @Override
    protected void init() {
        setWindowSize(getWindowWidth(), getWindowHeight());
        int sizeX = windowWidth;
        int sizeY = windowHeight;
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;

        assert minecraft != null;
        IconButton confirmButton = new IconButton(x + sizeX - 33, y + sizeY - 25, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> minecraft.setScreen(null));
        confirmButton.setToolTip(CreateLang.translate("gui.factory_panel.save_and_close")
                .component());
        addRenderableWidget(confirmButton);

        IconButton deleteButton = new IconButton(x + sizeX - 55, y + sizeY - 25, AllIcons.I_TRASH);
        deleteButton.withCallback(() -> {
            sendReset = true;
            minecraft.setScreen(null);
        });
        deleteButton.setToolTip(CreateLang.translate("gui.factory_panel.reset")
                .component());
        addRenderableWidget(deleteButton);

        IconButton newInputButton = new IconButton(x + 7, y + sizeY - 25, AllIcons.I_ADD);
        newInputButton.withCallback(() -> {
            FactoryPanelConnectionHandler.startConnection(behaviour);
            minecraft.setScreen(null);
        });
        newInputButton.setToolTip(CreateLang.translate("gui.factory_panel.connect_input")
                .component());
        addRenderableWidget(newInputButton);

        IconButton relocateButton = new IconButton(x + 29, y + sizeY - 25, AllIcons.I_MOVE_GAUGE);
        relocateButton.withCallback(() -> {
            FactoryPanelConnectionHandler.startRelocating(behaviour);
            minecraft.setScreen(null);
        });
        relocateButton.setToolTip(CreateLang.translate("gui.factory_panel.relocate")
                .component());
        addRenderableWidget(relocateButton);
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        graphics.drawCenteredString(font, title, x+windowWidth/2, y + 4, -1);
        graphics.blit(getTexture(), x, y, 0,0, windowWidth, windowHeight);
        GuiGameElement.of(behaviour.getItem().getDefaultInstance())
                .scale(4)
                .at(0, 0, -200)
                .render(graphics, x + windowWidth, y+windowHeight-48);
    }

    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public void removed() {
        sendIt(null);
        super.removed();
    }

    //TODO: Add connection removal option?
    private void sendIt(@SuppressWarnings("SameParameterValue") @Nullable FactoryPanelPosition toRemove) {
        FactoryPanelPosition pos = behaviour.getPanelPosition();
        FactoryPanelConfigurationPacket packet = new FactoryPanelConfigurationPacket(
                pos,
                "",
                new HashMap<>(),
                Lists.newArrayList(),
                0,
                0,
                toRemove,
                false,
                sendReset,
                false);
        AllPackets.getChannel().sendToServer(packet);
    }
}
