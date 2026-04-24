package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.minecraft.core.Direction;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class LogicGaugePonder implements Ponder {

    @Override
    public String getSchematicPath() {
        return "high_logistics/logic_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "logic_gauge", 9);

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

        // SPECIFIC

        var lever = util.select().fromTo(2,2,7,2,2,6);
        var out = util.select().fromTo(2,2,1, 1,3,1);

        scene.world().showIndependentSection(lever, Direction.WEST);
        scene.idle(10);
        scene.world().showIndependentSection(out, Direction.WEST);

        scene.idle(30);

        displayText(scene, util.grid().at(2,2,6), 80, true);

        displayText(scene, util.grid().at(2,2,6), 60, false);

        addPanelConnection(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,2,6), FactoryPanelBlock.PanelSlot.TOP_LEFT));
        scene.idle(10);
        addPanelConnection(scene, gaugeB, new FactoryPanelPosition(util.grid().at(2,2,1), FactoryPanelBlock.PanelSlot.TOP_LEFT));

        scene.idle(30);
        displayText(scene, util.grid().at(2,2,5), 60, false);
        scene.idle(10);

        scene.world().toggleRedstonePower(lever);
        scene.effects().indicateRedstone(util.grid().at(2,2,7));

        scene.idle(10);
        setPanelPowered(scene, gaugeA, false);
        scene.idle(10);
        setPanelPowered(scene, gaugeB, false);
        scene.world().toggleRedstonePower(out);
        scene.idle(50);

        scene.addKeyframe();
        var lever1 = util.select().fromTo(2,3,7,2,3,6);

        scene.world().showIndependentSection(lever1, Direction.WEST);

        scene.idle(10);
        addPanelConnection(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,3,6), FactoryPanelBlock.PanelSlot.TOP_LEFT));
        setArrowMode(scene, gaugeA, new FactoryPanelPosition(util.grid().at(2,3,6), FactoryPanelBlock.PanelSlot.TOP_LEFT), 2);
        scene.idle(30);


        displayText(scene, util.grid().at(2,2,5), 60, false);
        setPanelPowered(scene, gaugeA, true);
        scene.idle(10);
        setPanelPowered(scene, gaugeB, true);
        scene.world().toggleRedstonePower(out);
        scene.idle(20);
        displayText(scene, util.grid().at(2,3,5), 60, false);

        scene.world().toggleRedstonePower(lever1);
        scene.effects().indicateRedstone(util.grid().at(2,3,7));

        scene.idle(10);
        displayText(scene, util.grid().at(2,2,5), 60, false);

        setPanelPowered(scene, gaugeA, false);
        scene.idle(10);
        setPanelPowered(scene, gaugeB, false);
        scene.world().toggleRedstonePower(out);
        scene.idle(50);
    }
}
