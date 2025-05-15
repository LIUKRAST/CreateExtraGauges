package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.logistics.board.BasicPanelScreen;
import net.liukrast.eg.api.logistics.board.PanelConnections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ComparatorPanelScreen extends BasicPanelScreen {
    public static final ResourceLocation TEXTURE = ExtraGauges.id("textures/gui/comparator_gauge.png");

    private int current;
    private IconButton[] buttons;
    public ComparatorPanelScreen(ComparatorPanelBehaviour behaviour) {
        super(Component.translatable("create.logistics.comparator_gauge.title"), behaviour);
    }

    @Override
    protected void init() {
        super.init();
        int x = guiLeft;
        int y = guiTop;
        buttons = new IconButton[ComparatorMode.values().length];
        for(int i = 0; i < ComparatorMode.values().length; i++) {
            var mode = ComparatorMode.values()[i];
            int fX = x + windowWidth/2 + i*32 - (ComparatorMode.values().length*16) + 3;
            IconButton button = new IconButton(fX, y + windowHeight/3, mode.getIcon());
            int finalI = i;
            button.withCallback(() -> activateButton(finalI));
            button.setToolTip(CreateLang.translate(mode.getTranslationKey())
                    .component());
            if(((ComparatorPanelBehaviour)this.behaviour).comparatorMode == i) {
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
        return 88;
    }

    @Override
    public int getWindowWidth() {
        return 198;
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        //ChatFormatting.RED
        var value = behaviour.getConnectionValue(PanelConnections.REDSTONE).orElse(0) > 0;
        graphics.drawCenteredString(font, Component.literal("input " + ComparatorMode.values()[current].character() + " value").withStyle(value ? ChatFormatting.DARK_GREEN : ChatFormatting.RED), guiLeft + windowWidth/2-3, guiTop+windowHeight-21, -1);
    }
}
