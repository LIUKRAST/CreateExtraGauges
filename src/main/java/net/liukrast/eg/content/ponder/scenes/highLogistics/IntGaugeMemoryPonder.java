package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;
import static net.liukrast.eg.content.ponder.scenes.highLogistics.IntGaugePonder.*;

public class IntGaugeMemoryPonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/integer_gauge_memory";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "integer_gauge_memory", 5);

        scene.addInstruction(new RotateSceneInstruction(30, 100, true));
        scene.world().showIndependentSection(util.select().fromTo(1,1,4,1,4,0), Direction.DOWN);
        scene.idle(30);

        scene.world().showIndependentSection(util.select().fromTo(2,1,3,2,3,1), Direction.WEST);

        scene.idle(30);
        var gaugeA = new FactoryPanelPosition(util.grid().at(2,2,1), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);

        var nixie = util.grid().at(1,4,1);
        var lever = util.grid().at(2,1,3);
        var in = util.grid().at(2,2,3);

        int time = 60;
        builder.overlay()
                .showText(time)
                .text("")
                .placeNearTarget()
                .pointAt(gaugeA.pos().getCenter());
        builder.idle(time+20);
        time = 80;
        builder.overlay()
                .showText(time)
                .text("")
                .placeNearTarget()
                .pointAt(gaugeA.pos().south().getCenter());
        builder.idle(time+20);

        scene.overlay().showControls(in.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("3")));
        scene.idle(40);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("03"), 1, Direction.DOWN);

        scene.idle(40);
        scene.addKeyframe();

        scene.world().toggleRedstonePower(util.select().position(lever));
        scene.effects().indicateRedstone(lever);
        setPanelPowered(scene, gaugeA, true);
        setLinkTransmit(scene, lever, 1);

        scene.idle(30);

        displayText(scene, lever.north(), 60, false);

        builder.overlay()
                .showText(time)
                .text("")
                .placeNearTarget()
                .pointAt(gaugeA.pos().getCenter());
        builder.idle(time+20);


        scene.overlay().showControls(in.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("12")));
        scene.idle(40);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_CONFIG_LOCKED);
        scene.idle(50);

        displayText(scene, lever, 60, false);
        scene.world().toggleRedstonePower(util.select().position(lever));
        setPanelPowered(scene, gaugeA, false);
        setLinkTransmit(scene, lever, 0);

        scene.idle(20);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("12"), 1, Direction.DOWN);


    }
}
