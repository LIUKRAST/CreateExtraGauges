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
import net.liukrast.eg.EGConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BasicPanelScreen<T extends AbstractPanelBehaviour> extends AbstractSimiScreen {
    public static final ResourceLocation TEXTURE = EGConstants.id("textures/gui/generic_gauge.png");

    public final T behaviour;
    private boolean sendReset;

    public BasicPanelScreen(T behaviour) {
        this(behaviour.getDisplayName(), behaviour);
    }

    public BasicPanelScreen(Component component, T behaviour) {
        super(component);
        this.behaviour = behaviour;
    }

    public int getWindowWidth() {
        return 0;
    }

    public int getWindowHeight() {
        return 0;
    }

    @Override
    protected void init() {
        setWindowSize(getWindowWidth() + 106, getWindowHeight() + 46);
        int sizeX = windowWidth;
        int sizeY = windowHeight;
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;

        assert minecraft != null;
        IconButton confirmButton = new IconButton(x+sizeX-25, y+sizeY-24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onConfirm);
        confirmButton.setToolTip(CreateLang.translate("gui.factory_panel.save_and_close")
                .component());
        addRenderableWidget(confirmButton);

        IconButton deleteButton = new IconButton(x+sizeX-47, y+sizeY-24, AllIcons.I_TRASH);
        deleteButton.withCallback(() -> {
            sendReset = true;
            minecraft.setScreen(null);
        });
        deleteButton.setToolTip(CreateLang.translate("gui.factory_panel.reset")
                .component());
        addRenderableWidget(deleteButton);

        IconButton newInputButton = new IconButton(x + 7, y + sizeY - 24, AllIcons.I_ADD);
        newInputButton.withCallback(() -> {
            FactoryPanelConnectionHandler.startConnection(behaviour);
            minecraft.setScreen(null);
        });
        newInputButton.setToolTip(CreateLang.translate("gui.factory_panel.connect_input")
                .component());
        addRenderableWidget(newInputButton);

        IconButton relocateButton = new IconButton(x + 29, y + sizeY - 24, AllIcons.I_MOVE_GAUGE);
        relocateButton.withCallback(() -> {
            FactoryPanelConnectionHandler.startRelocating(behaviour);
            minecraft.setScreen(null);
        });
        relocateButton.setToolTip(CreateLang.translate("gui.factory_panel.relocate")
                .component());
        addRenderableWidget(relocateButton);
    }

    public void onConfirm() {
        assert minecraft != null;
        minecraft.setScreen(null);
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        graphics.drawCenteredString(font, title, x+windowWidth/2, y + 4, 0x3D3C48);
        graphics.blit(TEXTURE,x, y, 0, 0, 53, 16);
        graphics.blit(TEXTURE,x+windowWidth-106+53, y, 139, 0, 53, 16);

        graphics.blit(TEXTURE,x, y+windowHeight-46+15, 0, 56, 53, 32);
        graphics.blit(TEXTURE,x+windowWidth-106+53, y+windowHeight-46+15, 139, 56, 60, 32);

        if(windowWidth > 106) {
            int r = windowWidth-106;
            int r1 = r-3;
            int step = 0;
            if(r > 1) graphics.blit(TEXTURE,x+windowWidth-106+52, y+windowHeight-46+15,138,56,1,32);
            graphics.blit(TEXTURE, x+53,y+windowHeight-46+15,53,56,2,32);
            while(r > 0 || r1 > 0) {
                if(r>0)graphics.blit(TEXTURE, x + 53 + step*86, y, 53, 0, Math.min(r, 86), 16);
                if(r1>0)graphics.blit(TEXTURE,x+55+step*83,y+windowHeight-46+15,55,56,Math.min(r1,83),32);
                step++;
                r-=86;
                r1-=83;
            }
        }

        if(windowHeight > 47) {
            int r = windowHeight-47;
            int step = 0;
            graphics.blit(TEXTURE,x,y+16,0,16,53,Math.min(r, 40));
            graphics.blit(TEXTURE,x+windowWidth-106+53,y+16,139,16,53,Math.min(r, 40));
            if(windowWidth > 106) {
                int r1 = windowWidth-106;
                int step1 = 0;
                while(r1>0) {
                    graphics.blit(TEXTURE, x + 53 + step1*86, y+16, 53, 16, Math.min(r1, 86), Math.min(r, 40));
                    step1++;
                    r1-=86;
                }
            }
            r-=40;
            while(r>0) {
                graphics.blit(TEXTURE,x,y+56+step*36,0,20,53,Math.min(r, 36));
                graphics.blit(TEXTURE,x+windowWidth-106+53,y+56+step*36,139,20,53,Math.min(r, 36));
                int r1 = windowWidth-106;
                int step1 = 0;
                while(r1>0) {
                    graphics.blit(TEXTURE, x + 53 + step1*86, y+56+step*36, 53, 20, Math.min(r1, 86), Math.min(r, 36));
                    step1++;
                    r1-=86;
                }
                step++;
                r-=36;
            }
        }

        GuiGameElement.of(behaviour.getItem().getDefaultInstance())
                .scale(4)
                .at(0, 0, -200)
                .render(graphics, x + windowWidth, y+windowHeight-48);
    }

    @Override
    public void removed() {
        sendIt(null);
        super.removed();
    }

    //Add connection removal option?
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
