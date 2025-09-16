package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.liukrast.eg.content.ponder.scenes.highLogistics.GaugeHelper.*;

public class PassiveGaugeScene {
    public static void passiveGauge(SceneBuilder builder, SceneBuildingUtil util) {
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
        var plaG = new FactoryPanelPosition(new BlockPos(5,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);

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
                        .position(plaG.pos()), Direction.SOUTH);
        scene.idle(5);
        scene.world()
                .showSection(util.select()
                        .position(pickG.pos()), Direction.SOUTH);
        scene.idle(15);
        addPanelConnection(builder, pickG, diaG);
        scene.idle(5);
        addPanelConnection(builder, pickG, stickG);
        scene.idle(5);
        addPanelConnection(builder, stickG, plaG);
        scene.idle(20);

        scene.overlay()
                .showText(60)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(stickG.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(80);
        scene.overlay()
                .showText(60)
                .text("")
                .placeNearTarget()
                .pointAt(stickG.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        setConnectionAmount(builder, pickG, stickG, 2);
        setPanelNotSatisfied(builder, pickG);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(pickG.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .pointAt(stickG.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        setPanelCrafting(builder, util, pickG);
        scene.idle(40);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(stickG.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        setPanelSatisfied(builder, pickG);
        scene.idle(40);
    }
}
