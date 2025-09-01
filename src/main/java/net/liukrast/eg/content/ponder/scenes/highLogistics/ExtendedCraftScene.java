package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import static net.liukrast.deployer.lib.util.ponder.GaugeHelper.*;

public class ExtendedCraftScene {
    public static void autoCrafter(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "expanded_factory_recipes");
        var gauge = new FactoryPanelPosition(new BlockPos(0,3,4), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        var diamond = new FactoryPanelPosition(new BlockPos(0,3,2), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        var stick = new FactoryPanelPosition(new BlockPos(0,2,2), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        scene.world().showIndependentSection(util.select().fromTo(1,1,2,0,4,5), Direction.WEST);
        scene.idle(20);
        displayText(scene, gauge.pos(), 80, true);
        scene.world().showIndependentSection(util.select().fromTo(5,1,1,1,5,1),Direction.NORTH);
        scene.idle(20);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new BlockPos(3,3,1), new AABB(6,1,1,1,6,2), 60);
        displayText(scene, new BlockPos(3,3,0), 60, true);
        setPanelItem(scene, gauge, Items.DIAMOND_PICKAXE.getDefaultInstance());
        scene.idle(5);
        removePanelConnections(scene, gauge);
        addPanelConnection(scene, gauge, diamond);
        addPanelConnection(scene, gauge, stick);
        scene.idle(5);
        setPanelItem(scene, stick, Items.STICK.getDefaultInstance());
        scene.idle(5);
        setPanelItem(scene, diamond, Items.DIAMOND.getDefaultInstance());
        scene.idle(20);
        builder.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, new BlockPos(3,3,1), new AABB(6, 3, 1, 3, 6, 2), 60);
        scene.idle(30);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new BlockPos(3,3,1), new AABB(6, 3, 1, 1, 6, 2), 60);
        scene.idle(90);
        builder.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(new BlockPos(3,3,1).getCenter().add(-0.25f, 0.25f,0));
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new BlockPos(3,3,1), new AABB(6, 3, 1, 3, 6, 2), 30);
        scene.idle(30);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new BlockPos(3,3,1), new AABB(6, 2, 1, 3, 6, 2), 30);
        scene.idle(30);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, new BlockPos(3,3,1), new AABB(6, 1, 1, 3, 6, 2), 30);
        scene.idle(60);

    }
}
