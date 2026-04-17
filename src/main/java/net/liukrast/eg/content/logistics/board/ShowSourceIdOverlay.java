package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.liukrast.deployer.lib.DeployerClient;
import net.liukrast.eg.EGLang;
import net.liukrast.eg.content.logistics.board.comparator.ComparatorPanelBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ShowSourceIdOverlay implements IHaveHoveringInformation {
    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if(DeployerClient.SELECTED_CONNECTION == null) return false;
        assert Minecraft.getInstance().level != null;
        var fpb = FactoryPanelBehaviour.at(Minecraft.getInstance().level, DeployerClient.SELECTED_SOURCE);
        if(!(fpb instanceof ComparatorPanelBehaviour comp && comp.advanced) && !(fpb instanceof ExpressionPanelBehaviour)) return false;
        EGLang.translate("gui.variable_connection.info_header")
                .color(BaseConfigScreen.COLOR_TITLE_A)
                .forGoggles(tooltip);
        EGLang.translate("gui.variable_connection.name")
                .style(ChatFormatting.GRAY)
                .add(EGLang.builder()
                        .add(Component.literal(" " + (char)('a' + DeployerClient.SELECTED_CONNECTION.amount)))
                        .color(BaseConfigScreen.COLOR_TITLE_C)
                )
                .forGoggles(tooltip);
        return true;
    }
}
