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


public class ExpressionGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/expression_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "expression_gauge", 9);

        scene.addInstruction(new RotateSceneInstruction(30, 100, true));
        scene.world().showIndependentSection(util.select().fromTo(1,3,8,1,1,0), Direction.DOWN);
        scene.idle(5);

        FactoryPanelPosition gauge = new FactoryPanelPosition(util.grid().at(2,2,4), FactoryPanelBlock.PanelSlot.TOP_RIGHT);

        var inA = util.grid().at(2,2,7);
        var inB = util.grid().at(2,1,7);
        var display = util.grid().at(2,3,1);
        var nixie = util.grid().at(1,4,7);
        var lever = util.grid().at(2,1,3);

        scene.idle(30);
        scene.world().showIndependentSection(util.select().position(gauge.pos()), Direction.WEST);
        scene.idle(40);

        displayText(scene, gauge.pos(), 80, false);

        scene.world().showIndependentSection(util.select().fromTo(inA, inB), Direction.WEST);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().position(display), Direction.WEST);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().fromTo(nixie, nixie.north(6)), Direction.DOWN);
        scene.idle(40);

        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, inA, 60, false);

        addPanelConnection(scene, gauge, new FactoryPanelPosition(inA, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        scene.idle(30);
        addPanelConnection(scene, gauge, new FactoryPanelPosition(inB, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        setArrowMode(scene, gauge, new FactoryPanelPosition(inB, FactoryPanelBlock.PanelSlot.TOP_RIGHT), 1);

        scene.idle(30);
        displayText(scene, display, 60, false);
        addPanelConnection(scene, gauge, new FactoryPanelPosition(display, FactoryPanelBlock.PanelSlot.TOP_RIGHT));

        scene.idle(30);
        displayText(scene, inA.north(), 80, true);

        scene.overlay().showControls(inA.north().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("a")));
        scene.idle(40);
        scene.overlay().showControls(inB.north().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("b")));
        scene.idle(40);

        displayText(scene, gauge.pos(), 80, true);
        displayText(scene, gauge.pos(), 80, false);

        scene.overlay().showControls(inA.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("15")));
        scene.idle(40);
        scene.overlay().showControls(inB.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("2")));
        scene.idle(40);

        scene.world().flashDisplayLink(display);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("14.28"), 7, Direction.NORTH);

        scene.idle(40);
        scene.addKeyframe();
        scene.idle(20);

        scene.world().showIndependentSection(util.select().position(lever), Direction.WEST);
        scene.idle(10);

        addPanelConnection(scene, gauge, new FactoryPanelPosition(lever, FactoryPanelBlock.PanelSlot.TOP_LEFT));

        scene.idle(10);
        displayText(scene, lever, 60, false);

        displayText(scene, gauge.pos(), 60, false);
        scene.world().toggleRedstonePower(util.select().position(lever));
        IntGaugePonder.setLinkTransmit(scene, lever, 1);
        scene.effects().indicateRedstone(lever);
        scene.idle(10);

        scene.world().flashDisplayLink(display);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("0"), 7, Direction.NORTH);

        scene.idle(50);
    }
}
