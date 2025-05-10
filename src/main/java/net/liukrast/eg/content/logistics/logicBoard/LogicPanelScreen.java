package net.liukrast.eg.content.logistics.logicBoard;

import com.google.common.collect.Lists;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConfigurationPacket;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.RegisterItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LogicPanelScreen extends AbstractSimiScreen {
    public static final ResourceLocation TEXTURE = ExtraGauges.id("textures/gui/logic_gauge.png");

    private final FactoryPanelBehaviour behaviour;
    private boolean sendReset;

    public LogicPanelScreen(FactoryPanelBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    protected void init() {
        int sizeX = 112;
        int sizeY = 32;
        setWindowSize(112, 32);
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
        graphics.blit(TEXTURE, x, y, 0,0, 112, 32);
        GuiGameElement.of(RegisterItems.LOGIC_GAUGE.toStack())
                .scale(4)
                .at(0, 0, -200)
                .render(graphics, x + 100, y-16);
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
        CatnipServices.NETWORK.sendToServer(packet);
    }
}
