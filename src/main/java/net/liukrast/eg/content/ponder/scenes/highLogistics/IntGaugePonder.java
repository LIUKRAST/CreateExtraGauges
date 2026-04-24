package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.eg.content.logistics.link.LinkedLeverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class IntGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/integer_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "integer_gauge", 9);

        scene.addInstruction(new RotateSceneInstruction(30, 100, true));
        scene.world().showIndependentSection(util.select().fromTo(1,3,8,1,1,0), Direction.DOWN);
        scene.idle(5);

        FactoryPanelPosition gaugeA = new FactoryPanelPosition(util.grid().at(2,2,5), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        FactoryPanelPosition gaugeB = new FactoryPanelPosition(util.grid().at(2,2,3), FactoryPanelBlock.PanelSlot.TOP_LEFT);

        scene.idle(30);
        scene.world().showIndependentSection(util.select().position(gaugeA.pos()), Direction.WEST);
        scene.idle(40);

        displayText(scene, gaugeA.pos(), 80, false);

        scene.world().showIndependentSection(util.select().position(gaugeB.pos()), Direction.WEST);
        scene.idle(40);
        displayText(scene, gaugeB.pos(), 60, false);
        displayText(scene, gaugeB.pos(), 60, false);
        displayText(scene, gaugeA.pos(), 60, true);

        addPanelConnection(builder, gaugeB, gaugeA);

        scene.idle(30);
        displayText(scene, gaugeA.pos().north(), 60, false);

        scene.idle(30);

        var in = util.select().fromTo(2,3,7,2,1,7);
        var out = util.grid().at(2,3,1);
        var nixie = util.grid().at(1,4,1);

        scene.world().showIndependentSection(in, Direction.WEST);
        scene.idle(10);
        scene.world().showIndependentSection(util.select().position(out), Direction.WEST);
        scene.world().showIndependentSection(util.select().position(nixie), Direction.DOWN);
        scene.idle(30);

        displayText(scene, util.grid().at(2,2,7), 80, true);

        displayText(scene, util.grid().at(2,2,7), 60, false);

        addPanelConnection(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,2,7), FactoryPanelBlock.PanelSlot.TOP_LEFT));
        scene.idle(10);
        addPanelConnection(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,3,7), FactoryPanelBlock.PanelSlot.TOP_LEFT));
        setArrowMode(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,3,7), FactoryPanelBlock.PanelSlot.TOP_LEFT), 2);
        scene.idle(10);
        addPanelConnection(scene, gaugeB, new FactoryPanelPosition(out, FactoryPanelBlock.PanelSlot.BOTTOM_LEFT));

        scene.idle(30);
        displayText(scene, util.grid().at(2,2,6), 60, false);
        scene.addKeyframe();
        scene.idle(10);
        var analog = util.grid().at(2,1,7);
        var selector = util.grid().at(2,3,7);
        setAnalogLever(scene, util.select().position(analog), 3);
        scene.effects().indicateRedstone(analog);

        scene.overlay().showControls(analog.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("3")));
        scene.idle(40);
        scene.overlay().showControls(selector.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("5")));
        scene.idle(40);
        scene.overlay().showControls(gaugeA.pos().getCenter().add(0, 0.5, -0.5), Pointing.DOWN, 20).showing(AllIcons.I_ADD);
        scene.idle(40);

        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("8"), 1, Direction.DOWN);
        scene.idle(40);

        displayText(scene, gaugeA.pos(), 60, true);
        displayText(scene, gaugeA.pos().north(), 60, false);

        scene.overlay().showControls(gaugeA.pos().getCenter().add(0, 0.5, -0.5), Pointing.DOWN, 20).showing(AllIcons.I_MTD_CLOSE);
        scene.idle(40);

        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("15"), 1, Direction.DOWN);
        scene.idle(40);

        displayText(scene, gaugeA.pos(), 100, true);
        displayText(scene, gaugeA.pos(), 80, false);

        var lever = util.grid().at(2,1,3);
        scene.addKeyframe();

        scene.world().showIndependentSection(util.select().position(lever), Direction.WEST);
        scene.idle(30);

        addPanelConnection(scene, gaugeB, new FactoryPanelPosition(lever, FactoryPanelBlock.PanelSlot.TOP_LEFT));
        scene.idle(30);

        scene.world().toggleRedstonePower(util.select().position(lever));
        scene.effects().indicateRedstone(lever);
        setPanelPowered(scene, gaugeB, true);
        setLinkTransmit(scene, lever, 1);

        displayText(scene, lever, 60, false);
        displayText(scene, gaugeB.pos(), 60, false);
        scene.idle(10);
        scene.overlay().showControls(nixie.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setNixieTubeText(scene, nixie, Component.literal("0"), 1, Direction.DOWN);
        scene.idle(50);

    }

    public static void setLinkTransmit(SceneBuilder scene, BlockPos pos, int signal) {
        scene.world().modifyBlockEntity(pos, LinkedLeverBlockEntity.class, be -> be.transmittedSignal = signal);
    }


}
