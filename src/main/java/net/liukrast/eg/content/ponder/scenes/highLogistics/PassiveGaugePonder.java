package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class PassiveGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/passive_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("passive_gauge", "Using Passive Gauges to optimize Automated Recipes");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        var pickG = new FactoryPanelPosition(new BlockPos(2,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);
        var stickG = new FactoryPanelPosition(new BlockPos(3,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);
        var diaG = new FactoryPanelPosition(new BlockPos(3,4,2), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);
        var plankG = new FactoryPanelPosition(new BlockPos(5,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);

        scene.world().showIndependentSection(util.select().fromTo(5,4,3,1,1,3), Direction.NORTH);
        scene.idle(10);
        removePanelConnections(builder, pickG);
        setPanelPassive(builder, pickG);

        removePanelConnections(builder, stickG);
        setPanelPassive(builder, stickG);

        scene.world()
                .showSection(util.select()
                        .position(diaG.pos()), Direction.SOUTH);
        scene.idle(5);
        scene.world()
                .showSection(util.select()
                        .position(stickG.pos()), Direction.SOUTH);
        scene.idle(5);
        scene.world()
                .showSection(util.select()
                        .position(plankG.pos()), Direction.SOUTH);
        scene.idle(5);
        scene.world()
                .showSection(util.select()
                        .position(pickG.pos()), Direction.SOUTH);
        scene.idle(15);
        addPanelConnection(builder, pickG, diaG);
        scene.idle(5);
        addPanelConnection(builder, pickG, stickG);
        scene.idle(5);
        addPanelConnection(builder, stickG, plankG);
        scene.idle(20);

        displayText(scene, stickG, Direction.NORTH, 60, true);
        displayText(scene, stickG, Direction.NORTH, 60, false);
        scene.idle(20);
        setConnectionAmount(builder, pickG, stickG, 2);
        setPanelNotSatisfied(builder, pickG);
        displayText(scene, pickG, Direction.NORTH, 100, true);
        displayText(scene, stickG, Direction.NORTH, 100, false);
        setPanelCrafting(builder, util, pickG);
        scene.idle(40);
        displayText(scene, stickG, Direction.NORTH, 100, true);
        setPanelSatisfied(builder, pickG);
        scene.idle(40);
    }
}
