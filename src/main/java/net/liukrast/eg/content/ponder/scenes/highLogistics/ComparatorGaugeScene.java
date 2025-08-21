package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class ComparatorGaugeScene {

    public static void comparatorGauge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("comparator_gauge", "Using Comparator Gauges to convert numbers into booleans");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);

        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        scene.world().showIndependentSection(util.select().fromTo(5,1,3,1,5,2), Direction.NORTH);
        scene.idle(40);

        var gaugePos = new BlockPos(3,3,2);
        var originPos = new BlockPos(5,3,2);
        var outPos = new BlockPos(1,3,2);

        scene.overlay()
                .showText(120)
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.overlay()
                .showControls(gaugePos.getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 100)
                .rightClick();
        scene.overlay()
                .showText(120)
                .attachKeyFrame()
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.overlay()
                .showText(120)
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.overlay()
                .showText(120)
                .attachKeyFrame()
                .text("")
                .placeNearTarget()
                .pointAt(originPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.overlay()
                .showText(120)
                .text("")
                .placeNearTarget()
                .pointAt(originPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.effects().indicateRedstone(outPos);
        scene.world().toggleRedstonePower(util.select().position(outPos));
        scene.world().modifyBlockEntity(gaugePos, FactoryPanelBlockEntity.class,be -> be.panels.get(FactoryPanelBlock.PanelSlot.TOP_LEFT).redstonePowered = false);
        scene.idle(40);
    }
}
