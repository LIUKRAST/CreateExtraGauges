package net.liukrast.eg.content.logistics.board.comparator;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.logistics.board.screen.BasicPanelScreen;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.networking.ComparatorAdvancedSetupPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ComparatorPanelScreen extends BasicPanelScreen<ComparatorPanelBehaviour> {

    public static final ResourceLocation TEXTURE = EGConstants.id("textures/gui/comparator_gauge.png");

    private int[] right,left;
    private int[] variables;
    private final boolean advanced;
    private int selected = -1;

    private int current;
    private EditBox numberBox;
    public ComparatorPanelScreen(ComparatorPanelBehaviour behaviour) {
        super(behaviour);
        current = behaviour.comparatorMode.ordinal();
        var triple = behaviour.getInputs();
        variables = triple.a;
        right = triple.b;
        left = triple.c;
        this.advanced = behaviour.advanced;
    }

    @Override
    protected void init() {
        super.init();
        if(advanced) return;
        int x = guiLeft;
        int y = guiTop;
        String oldOrDefault = numberBox == null ? "" + behaviour.value : numberBox.getValue();
        numberBox = new EditBox(font, x+89, y+56, 100, 20, Component.empty());
        numberBox.setValue(oldOrDefault);
        numberBox.setMaxLength(11);
        numberBox.setTextColor(0xFF545454);
        numberBox.setTextShadow(false);
        numberBox.setBordered(false);
        addRenderableWidget(numberBox);
    }

    @Override
    public void onConfirm() {
        CatnipServices.NETWORK.sendToServer(
                        new ValueSettingsPacket(
                                behaviour.blockEntity.getBlockPos(),
                                1, current,
                                null, null, Direction.UP,
                                AllKeys.ctrlDown(), behaviour.netId()
                        ));
        if(advanced) {
            CatnipServices.NETWORK.sendToServer(new ComparatorAdvancedSetupPacket(
                    behaviour.getPanelPosition(),
                    right,
                    left
            ));
            super.onConfirm();
            return;
        }
        try {
            int number = Integer.parseInt(numberBox.getValue());
            CatnipServices.NETWORK.sendToServer(
                    new ValueSettingsPacket(
                            behaviour.blockEntity.getBlockPos(),
                            2, number,
                            null, null, Direction.UP,
                            AllKeys.ctrlDown(), behaviour.netId()
                    ));
        } catch (NumberFormatException ignored) {}

        super.onConfirm();
    }

    @Override
    public int getWindowHeight() {
        return advanced ? 100 : 58;
    }

    @Override
    public int getWindowWidth() {
        return 100;
    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        graphics.blit(TEXTURE, guiLeft+30+43, guiTop+26, 43, 0, 12, 25);
        if(!advanced) {
            graphics.blit(TEXTURE, guiLeft+30, guiTop+26+25, 0, 25, 37, 20);
            graphics.blit(TEXTURE, guiLeft+30+55, guiTop+26+16, 55, 16, 91, 29);

        }
        graphics.blit(TEXTURE, guiLeft+68, guiTop+51, current*16, 45, 16, 20);
        if(mouseX >= guiLeft+68 && mouseX < guiLeft+68+16 && mouseY >= guiTop+51 && mouseY < guiTop+18)
            graphics.renderTooltip(font, Component.translatable(ComparingOperator.values()[current].getTranslationKey()), mouseX, mouseY);
        for(int i = 0; i < 6; i++) {
            int x = guiLeft+i*17+86;
            int y = guiTop+23;
            boolean hovered = mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+18;
            graphics.blit(TEXTURE, x, y, i*16, i == current ? 85 : hovered ? 65 : 45, 16, 20);
            if(hovered)
                graphics.renderTooltip(font, Component.translatable(ComparingOperator.values()[i].getTranslationKey()), mouseX, mouseY);
        }

        if(!advanced) return;
        guiTop+=6;
        graphics.blit(TEXTURE, guiLeft + 13, guiTop + 70, 0, 106, 54, 17);
        graphics.drawString(font, "variables:", guiLeft + 16, guiTop + 73, 0xb57074, false);
        for(int i = 0; i < variables.length; i++) {
            if(selected == variables[i]) continue;
            boolean hovered = selected == -1 && mouseX >= guiLeft + 68 + i*12 && mouseX < guiLeft + 68 + i*12+11 && mouseY >= guiTop + 70 && mouseY < guiTop + 70 + 17;
            graphics.blit(TEXTURE, guiLeft + 68 + i*12, guiTop + 70, 55, 106 + (hovered ? 18 : 0), 11, 17);
            graphics.drawString(font, ""+(char)('a'+variables[i]), guiLeft + 71 + i*12, guiTop + 73, 0xb57074, false);
        }
        boolean h = mouseX >= guiLeft + 68 + Math.max(1, variables.length)*12 && mouseX <= guiLeft + 68 + Math.max(1, variables.length)*12+10 && mouseY >= guiTop + 70 && mouseY <= guiTop + 70 + 10;

        graphics.blit(TEXTURE, guiLeft + 68 + Math.max(1, variables.length)*12, guiTop + 70, 67, h ? 122 : 109, 10, 12);
        if(h)
            graphics.renderTooltip(font, List.of(
                    Component.translatable("comparator_gauge.info.title").withStyle(BaseConfigScreen.COLOR_TITLE_C.asStyle()),
                    Component.translatable("comparator_gauge.info.line_1").withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("comparator_gauge.info.line_2").withStyle(ChatFormatting.DARK_GRAY)
            ), Optional.empty(), mouseX, mouseY);

        if(left.length == 0) {
            graphics.blit(TEXTURE, guiLeft + 56, guiTop + 47, 55, 106, 11, 17);
            graphics.drawString(font, "?", guiLeft + 59, guiTop + 50, 0xb57074, false);
        } else for(int i = 0; i < left.length; i++) {
            if(selected == left[i]) continue;
            int x = 56,y = 47;
            if(i != left.length-1) {
                graphics.blit(TEXTURE, guiLeft + x - i*18 - 6, guiTop + y+5, 2,2,5,5);
            }
            boolean hovered = isOnButton(guiLeft + x - i*18, guiTop + y, mouseX, mouseY);
            graphics.blit(TEXTURE, guiLeft + x - i*18, guiTop + y, 55, 106 + (hovered ? 18 : 0), 11, 17);
            graphics.drawString(font, ""+(char)('a'+left[i]), guiLeft + x+3 - i*18, guiTop + y+3, 0xb57074, false);
        }
        if(right.length == 0) {
            graphics.blit(TEXTURE, guiLeft + 85, guiTop + 47, 55, 106, 11, 17);
            graphics.drawString(font, "?", guiLeft + 88, guiTop + 50, 0xb57074, false);
        } else for(int i = 0; i < right.length; i++) {
            if(selected == right[i]) continue;
            int x = 85,y = 47;
            if(i != 0) {
                graphics.blit(TEXTURE, guiLeft + x + i*18 - 6, guiTop + y+5, 2,2,5,5);
            }
            boolean hovered = isOnButton(guiLeft + x + i*18, guiTop + y, mouseX, mouseY);
            graphics.blit(TEXTURE, guiLeft + x + i*18, guiTop + y, 55, 106 + (hovered ? 18 : 0), 11, 17);
            graphics.drawString(font, ""+(char)('a'+right[i]), guiLeft + x+3 + i*18, guiTop + y+3, 0xb57074, false);
        }

        if(selected == -1) return;
        graphics.blit(TEXTURE, mouseX-5, mouseY-2, 55, 106, 11, 17);
        graphics.drawString(font, ""+(char)('a'+selected), mouseX, mouseY, 0xb57074, false);
        graphics.renderOutline(guiLeft + 68, guiTop + 70, Math.max(1, variables.length)*12-1, 17, -1);

        graphics.renderOutline(guiLeft + 85, guiTop + 47, 11+Math.max(0, right.length-1)*18, 17, -1);

        graphics.renderOutline(guiLeft + 67 - 11-Math.max(0, left.length-1)*18, guiTop + 47, 11+Math.max(0, left.length-1)*18, 17, -1);

    }

    public boolean isOnButton(int x, int y, double mouseX, double mouseY) {
        if(selected != -1) return false;
        if(mouseX < x) return false;
        if(mouseX > x + 11) return false;
        if(mouseY < y) return false;
        return mouseY <= y + 11;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(int i = 0; i < 6; i++) {
            if(i == current) continue;
            int x = guiLeft+i*17+86;
            int y = guiTop+23;
            if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16) {
                current = i;
                return true;
            }
        }
        int guiTop = this.guiTop+6;
        if(selected != -1) return super.mouseClicked(mouseX, mouseY, button);
        for(int i = 0; i < variables.length; i++) {
            if(!(mouseX >= guiLeft + 68 + i*12 && mouseX < guiLeft + 68 + i*12+11 && mouseY >= guiTop + 70 && mouseY < guiTop + 70 + 17)) continue;
            this.selected = variables[i];
        }
        for(int i = 0; i < right.length; i++) {
            int x = 85,y = 47;
            if(!isOnButton(guiLeft + x + i*18, guiTop + y, mouseX, mouseY)) continue;
            this.selected = right[i];
        }
        for(int i = 0; i < left.length; i++) {
            int x = 56,y = 47;
            if(!isOnButton(guiLeft + x - i*18, guiTop + y, mouseX, mouseY)) continue;
            this.selected = left[i];
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(this.selected == -1) return super.mouseReleased(mouseX, mouseY, button);
        int guiTop = this.guiTop+6;
        if(mouseX >= guiLeft+68 && mouseX < guiLeft+68+Math.max(1, variables.length)*12-1 && mouseY >= guiTop + 70 && mouseY < guiTop + 70+17) {
            right = ArrayUtils.removeElement(right, selected);
            left = ArrayUtils.removeElement(left, selected);
            if(!ArrayUtils.contains(variables, selected)) {
                variables = ArrayUtils.add(variables, selected);
            }
        } else if(mouseX >= guiLeft+85 && mouseX < guiLeft+85+11+Math.max(0, right.length-1)*18 && mouseY >= guiTop + 47 && mouseY < guiTop + 47+17) {
            variables = ArrayUtils.removeElement(variables, selected);
            left = ArrayUtils.removeElement(left, selected);
            if(!ArrayUtils.contains(right, selected)) {
                right = ArrayUtils.add(right, selected);
            }
        } else if(mouseX >= guiLeft + 67 - 11-Math.max(0, left.length-1)*18 && mouseX < guiLeft + 67 - 11-Math.max(0, left.length-1)*18 + 11+Math.max(0, left.length-1)*18 &&
                mouseY >= guiTop + 47 && mouseY < guiTop + 47+17) {
            variables = ArrayUtils.removeElement(variables, selected);
            right = ArrayUtils.removeElement(right, selected);
            if(!ArrayUtils.contains(left, selected)) {
                left = ArrayUtils.add(left, selected);
            }
        }

        this.selected = -1;
        return true;
    }
}
