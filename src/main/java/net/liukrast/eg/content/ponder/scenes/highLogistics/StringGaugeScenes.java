package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class StringGaugeScenes {

    public static void displayCollector(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "display_collector");
        var observer = new BlockPos(4,1,2);
        var collector = new BlockPos(3,1,3);
        var collector1 = new BlockPos(5,3,2);
        var gauge = new FactoryPanelPosition(new BlockPos(3,3,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var t = scene.world().showIndependentSection(util.select().position(collector), Direction.DOWN);
        var dLink = new BlockPos(1,4,2);
        scene.idle(20);
        displayText(scene, collector, 60, true);
        scene.world().hideIndependentSection(t, Direction.UP);
        scene.idle(15);
        scene.world().setBlock(collector, AllBlocks.COPPER_SCAFFOLD.getDefaultState(), false);
        scene.world().showIndependentSection(util.select().fromTo(5,1,3,1,5,3), Direction.NORTH);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().fromTo(3,3,2,1,4,2), Direction.SOUTH);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().fromTo(5,1,2,4,1,2), Direction.UP);
        scene.idle(20);
        scene.overlay().showControls(observer.getCenter(), Pointing.RIGHT, 60).withItem(EGBlocks.DISPLAY_COLLECTOR.asItem().getDefaultInstance()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, observer, new AABB(observer), 60);
        scene.idle(15);
        displayText(scene, observer, 45, true);
        scene.world().showIndependentSection(util.select().position(collector1), Direction.SOUTH);
        displayText(scene, collector1, 60, false);
        addPanelConnection(scene, gauge, new FactoryPanelPosition(collector1, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, gauge.pos(), 20, true);
        scene.overlay().showControls(new BlockPos(5,1,2).getCenter(), Pointing.RIGHT, 60).withItem(Items.DIAMOND.getDefaultInstance());
        scene.idle(40);
        scene.world().flashDisplayLink(collector1);
        scene.idle(10);
        scene.world().flashDisplayLink(dLink);
        setNixieTubeText(scene, new BlockPos(5,5,3), Component.literal("1▪ Diamond"), 5, Direction.WEST);
    }
}
