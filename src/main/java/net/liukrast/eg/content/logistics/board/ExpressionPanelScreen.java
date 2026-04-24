package net.liukrast.eg.content.logistics.board;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.logistics.board.screen.BasicPanelScreen;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.networking.ExpressionPanelUpdatePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpressionPanelScreen extends BasicPanelScreen<ExpressionPanelBehaviour> {
    public static final ResourceLocation TEXTURE = ExtraGauges.CONSTANTS.id("textures/gui/expression_gauge.png");

    private EditBox expressionBox;
    private final char[] variables;
    private String error = null;

    public ExpressionPanelScreen(ExpressionPanelBehaviour behaviour) {
        super(behaviour);
        variables = behaviour.getInputs();
    }

    @Override
    protected void init() {
        super.init();
        int x = guiLeft;
        int y = guiTop;
        String oldOrDefault = expressionBox == null ? behaviour.getExpression() : expressionBox.getValue();
        expressionBox = new EditBox(font, x+39, y+54, 130, 20, Component.empty());
        expressionBox.setValue(oldOrDefault);
        expressionBox.setMaxLength(128);
        expressionBox.setTextColor(0xFF545454);
        expressionBox.setTextShadow(false);
        expressionBox.setBordered(false);
        expressionBox.setResponder(str -> evaluateExpression());
        addRenderableWidget(expressionBox);
        evaluateExpression();
    }

    public void evaluateExpression() {
        if(expressionBox == null) return;
        try {
            Map<String, Double> variables = IntStream.range(0, this.variables.length)
                    .mapToObj(i -> this.variables[i])
                    .collect(Collectors.toMap(
                            i -> "" + i,
                            i -> 1d
                    ));
            Expression expression = new ExpressionBuilder(expressionBox.getValue())
                    .variables(variables.keySet())
                    .functions(ExpressionPanelBehaviour.FUNCTIONS)
                    .build();

            variables.forEach(expression::setVariable);

            if(expression.validate().isValid()) {
                expression.evaluate();
                error = null;
            }
        } catch (Exception e) {
            this.error = e.getMessage();
        }

    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(TEXTURE, guiLeft + 4, guiTop + 17, 0, 0, 184, 26);
        graphics.blit(TEXTURE, guiLeft + 13, guiTop + 43, 0, 26, 159, 27);
        graphics.blit(TEXTURE, guiLeft + 13, guiTop + 70, 0, 53, 54, 17);
        graphics.drawString(font, "variables:", guiLeft + 16, guiTop + 73, 0xb57074, false);
        for(int i = 0; i < variables.length; i++) {
            graphics.blit(TEXTURE, guiLeft + 68 + i*12, guiTop + 70, 55, 53, 11, 17);
            graphics.drawString(font, ""+variables[i], guiLeft + 71 + i*12, guiTop + 73, 0xb57074, false);
        }
        boolean h = mouseX >= guiLeft + 68 + variables.length*12 && mouseX <= guiLeft + 68 + variables.length*12 + 10 && mouseY >= guiTop + 70 && mouseY <= guiTop + 70 + 10;
        graphics.blit(TEXTURE, guiLeft + 68 + variables.length*12, guiTop + 70, 67, 56, 10, 12);
        if(h)
            graphics.renderTooltip(font, List.of(
                    Component.translatable("expression_gauge.info.title").withStyle(BaseConfigScreen.COLOR_TITLE_C.asStyle()),
                    Component.translatable("expression_gauge.info.line_1").withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("expression_gauge.info.line_2").withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("expression_gauge.info.line_3").withStyle(ChatFormatting.DARK_GRAY)
            ), Optional.empty(), mouseX, mouseY);

        // Render error
        if (error == null) return;

        int maxBodyWidth = 178;
        var bodyLines = this.font.split(Component.literal(error), maxBodyWidth);

        if (bodyLines.size() > 2) {
            graphics.drawString(font, bodyLines.getFirst(), guiLeft + 9, guiTop + 20, 0x722c4b, false);

            String firstLine = font.substrByWidth(FormattedText.of(error), maxBodyWidth).getString();
            String remaining = error.substring(firstLine.length()).stripLeading();

            int ellipsisWidth = font.width("...");
            String secondLine = font.substrByWidth(FormattedText.of(remaining), maxBodyWidth - ellipsisWidth).getString() + "...";
            graphics.drawString(font, secondLine, guiLeft + 9, guiTop + 20 + font.lineHeight, 0x722c4b, false);

        } else {
            for (int i = 0; i < Math.min(bodyLines.size(), 2); i++) {
                graphics.drawString(font, bodyLines.get(i), guiLeft + 9, guiTop + 20 + i * font.lineHeight, 0x722c4b, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public int getWindowHeight() {
        return 100;
    }

    @Override
    public int getWindowWidth() {
        return 86;
    }

    @Override
    public void onConfirm() {
        CatnipServices.NETWORK.sendToServer(new ExpressionPanelUpdatePacket(behaviour.getPanelPosition(), expressionBox.getValue()));
        super.onConfirm();
    }
}
