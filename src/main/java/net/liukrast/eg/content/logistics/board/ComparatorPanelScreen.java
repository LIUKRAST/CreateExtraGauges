package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.api.logistics.board.BasicPanelScreen;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ComparatorPanelScreen extends BasicPanelScreen<ComparatorPanelBehaviour> {

    private int current;
    private IconButton[] buttons;
    public ComparatorPanelScreen(ComparatorPanelBehaviour behaviour) {
        super(behaviour);
    }

    @Override
    protected void init() {
        super.init();
        int x = guiLeft;
        int y = guiTop;
        buttons = new IconButton[ComparatorMode.values().length];
        for(int i = 0; i < ComparatorMode.values().length; i++) {
            var mode = ComparatorMode.values()[i];
            int fX = x + (windowWidth>>1) + i*32 - (ComparatorMode.values().length*16) + 3;
            IconButton button = new IconButton(fX, y + windowHeight/3, mode.getIcon());
            int finalI = i;
            button.withCallback(() -> activateButton(finalI));
            button.setToolTip(Component.translatable(mode.getTranslationKey()));
            if(this.behaviour.comparatorMode == i) {
                button.setActive(false);
                current = i;
            }
            addRenderableWidget(button);
            buttons[i] = button;
        }
    }

    private void activateButton(int i) {
        CatnipServices.NETWORK
                .sendToServer(
                        new ValueSettingsPacket(
                                behaviour.blockEntity.getBlockPos(),
                                2, i,
                                null, null, Direction.UP,
                                AllKeys.ctrlDown(), behaviour.netId()
                                ));
        for(var button : buttons) {
            button.setActive(true);
        }
        buttons[i].setActive(false);
        current = i;
    }

    @Override
    public int getWindowHeight() {
        return 40;
    }

    @Override
    public int getWindowWidth() {
        return 100;
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var value = behaviour.getConnectionValue(EGPanelConnections.REDSTONE).orElse(0) > 0;
        graphics.drawCenteredString(font,
                Component.literal((value ? "✔" : "✖") +" input " + ComparatorMode.values()[current].character() + " " + behaviour.value),
                guiLeft + (windowWidth>>1),
                guiTop+windowHeight-21,
                0x3D3C48
        );
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
    }
}
