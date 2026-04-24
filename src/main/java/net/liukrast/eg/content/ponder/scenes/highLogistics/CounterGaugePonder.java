package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.eg.content.logistics.board.IntPanelBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class CounterGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/counter_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "counter_gauge", 9);
        scene.addInstruction(new RotateSceneInstruction(30, 100, true));

        scene.world().showIndependentSection(util.select().fromTo(1,1,8,1,3,0), Direction.DOWN);

        scene.world().showIndependentSection(util.select().fromTo(2,2, 7, 2,2,1), Direction.WEST);

        scene.idle(50);

        var gauge = new FactoryPanelPosition(util.grid().at(2,2,4), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);

        var button = util.grid().at(2,2,7);
        var out = util.select().fromTo(2,2,1,1,3,1);

        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, button, 60, true);

        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 1);
        scene.effects().indicateRedstone(button);
        scene.overlay().showControls(gauge.pos().getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ADD);
        scene.idle(20);
        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 0);
        scene.idle(10);

        displayText(scene, gauge.pos(), 40, false);
        displayText(scene, gauge.pos(), 60, true);
        scene.overlay().showControls(gauge.pos().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("4")));
        scene.idle(40);
        for(int i = 0; i < 2; i++) {
            scene.world().toggleRedstonePower(util.select().position(button));
            IntGaugePonder.setLinkTransmit(scene, button, 1);
            scene.effects().indicateRedstone(button);
            scene.idle(20);
            scene.world().toggleRedstonePower(util.select().position(button));
            IntGaugePonder.setLinkTransmit(scene, button, 0);
            scene.idle(10);
        }

        displayText(scene, gauge.pos(), 60, true);

        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 1);
        scene.effects().indicateRedstone(button);
        scene.idle(5);

        setPanelPowered(scene, gauge, false);
        setGaugeCount(scene, gauge, 4);
        scene.world().toggleRedstonePower(out);

        scene.idle(15);

        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 0);

        scene.idle(20);
        displayText(scene, gauge.pos(), 60, false);
        scene.idle(40);

        // Reset

        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 1);
        scene.effects().indicateRedstone(button);
        scene.idle(5);
        setPanelPowered(scene, gauge, true);
        setGaugeCount(scene, gauge, 0);
        scene.world().toggleRedstonePower(out);
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 0);
        scene.idle(10);

        scene.addKeyframe();
        var quartz = util.grid().at(2,3,3);
        var nixie = util.grid().at(1,4,3);

        scene.world().showIndependentSection(util.select().position(quartz), Direction.WEST);
        scene.world().showIndependentSection(util.select().position(nixie), Direction.DOWN);
        scene.idle(20);
        addPanelConnection(scene, gauge, new FactoryPanelPosition(quartz, FactoryPanelBlock.PanelSlot.BOTTOM_LEFT));
        setArrowMode(scene, gauge, new FactoryPanelPosition(quartz, FactoryPanelBlock.PanelSlot.BOTTOM_LEFT), 3);

        scene.idle(50);


        displayText(scene, gauge.pos(), 60, false);

        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 1);
        scene.effects().indicateRedstone(button);
        scene.idle(5);

        setNixieTubeText(scene, nixie, Component.literal("01"), 1, Direction.DOWN);

        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        IntGaugePonder.setLinkTransmit(scene, button, 0);
        scene.idle(50);
    }


}
