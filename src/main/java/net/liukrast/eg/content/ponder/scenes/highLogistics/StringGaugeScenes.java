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

import static net.liukrast.eg.content.ponder.scenes.highLogistics.GaugeHelper.*;

public class StringGaugeScenes {
    public static void stringGauge(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "string_gauge");
        var gauge = new FactoryPanelPosition(new BlockPos(3,3,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var dLink = new FactoryPanelPosition(new BlockPos(1,4,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var extra = new FactoryPanelPosition(new BlockPos(5,3,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var nixie = new BlockPos(5,5,3);
        scene.world().showIndependentSection(util.select().fromTo(5,1,3,1,5,3), Direction.NORTH);
        scene.idle(5);
        removePanelConnections(builder, gauge);
        scene.world().showIndependentSection(util.select().fromTo(5,3,2,3,3,2), Direction.SOUTH);
        scene.world().showIndependentSection(util.select().position(dLink.pos()), Direction.SOUTH);
        scene.idle(5);
        addPanelConnection(builder, gauge, extra);
        addPanelConnection(builder, gauge, dLink);
        scene.idle(20);
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        setSignText(builder, extra.pos(), 0, Component.literal("Hello!"));
        scene.idle(10);
        scene.overlay()
                .showText(40)
                .text("")
                .placeNearTarget()
                .pointAt(extra.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(60);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(builder, nixie, Component.literal("Hello!"), 5, Direction.WEST);
        scene.idle(40);
        scene.addKeyframe();
        var sign1 = new FactoryPanelPosition(new BlockPos(5,4,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var sign2 = new FactoryPanelPosition(new BlockPos(5,2,2), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var sign1S = scene.world().showIndependentSection(util.select().position(sign1.pos()), Direction.SOUTH);
        addPanelConnection(builder, gauge, sign1);
        setArrowMode(builder, gauge, sign1, 3);
        scene.idle(5);
        var sign2S = scene.world().showIndependentSection(util.select().position(sign2.pos()), Direction.SOUTH);
        addPanelConnection(builder, gauge, sign2);
        setArrowMode(builder, gauge, sign2, 1);
        scene.idle(20);
        setSignText(builder, sign1.pos(), 0, Component.literal("A"));
        scene.idle(5);
        setSignText(builder, extra.pos(), 0, Component.literal("B"));
        scene.idle(5);
        setSignText(builder, sign2.pos(), 0, Component.literal("C"));
        scene.idle(15);
        scene.overlay()
                .showText(40)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(80);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(builder, nixie, Component.literal("ABC"), 5, Direction.WEST);
        scene.idle(20);
        scene.overlay()
                .showText(60)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(80);
        scene.overlay()
                .showControls(gauge.pos().getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 60)
                .rightClick();
        scene.overlay()
                .showText(60)
                .text("")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(80);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(builder, nixie, Component.literal("A,B,C"), 5, Direction.WEST);
        scene.idle(40);
        scene.addKeyframe();
        removePanelConnections(builder, gauge);
        addPanelConnection(builder, gauge, extra);
        addPanelConnection(builder, gauge, dLink);
        scene.world().hideIndependentSection(sign1S, Direction.NORTH);
        scene.world().hideIndependentSection(sign2S, Direction.NORTH);
        scene.idle(10);
        setSignText(builder, extra.pos(), 0, Component.literal("MyHouse"));
        scene.idle(10);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(builder, nixie, Component.literal("MyHouse"), 5, Direction.WEST);
        scene.idle(40);
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        scene.overlay()
                .showText(60)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(80);
        scene.overlay()
                .showText(40)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(60);
        scene.overlay()
                .showText(40)
                .text("")
                .placeNearTarget()
                .pointAt(gauge.pos().getCenter().add(-0.25f, 0.25f,0));
        scene.idle(60);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(builder, nixie, Component.literal("MyBase"), 5, Direction.WEST);
        scene.idle(20);
    }

    public static void stringGaugeStorage(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "string_gauge_storage");
        var gaugePos = new FactoryPanelPosition(new BlockPos(2,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);
        var inPos = new FactoryPanelPosition(new BlockPos(4,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);
        var dLink = new BlockPos(5,4,2);
        var outPos = new BlockPos(1,3,2);
        var intGauge = new FactoryPanelPosition(new BlockPos(3,4,2), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);
        scene.world().showIndependentSection(util.select().fromTo(5,1,3,1,5,3), Direction.NORTH);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().fromTo(5,1,2,1,3,2), Direction.SOUTH);
        scene.world().showIndependentSection(util.select().position(1,4,2), Direction.SOUTH);
        scene.world().showIndependentSection(util.select().position(dLink), Direction.SOUTH);
        scene.idle(20);
        displayText(builder, gaugePos.pos(), 60, true);
        displayText(builder, inPos.pos(), 60, false);
        scene.world().flashDisplayLink(dLink);
        setNixieTubeText(scene, new BlockPos(5,5,3), Component.literal("1▪ Diamond"), 5, Direction.WEST);
        scene.idle(40);
        displayText(builder, outPos, 100, true);
        scene.addKeyframe();
        scene.world().showIndependentSection(util.select().position(intGauge.pos()), Direction.SOUTH);
        removePanelConnections(scene, gaugePos);
        addPanelConnection(scene, gaugePos, inPos);
        scene.idle(15);
        addPanelConnection(scene, intGauge, gaugePos);
        addPanelConnection(scene, intGauge, new FactoryPanelPosition(dLink, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        scene.idle(20);
        displayText(builder, intGauge.pos(), 80, false);
        displayText(builder, gaugePos.pos(), 80, false);
        scene.world().flashDisplayLink(dLink);
        setNixieTubeText(scene, new BlockPos(5,5,3), Component.literal("12"), 5, Direction.WEST);
        displayText(builder, dLink, 80, false);
    }

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
        scene.overlay().showControls(observer.getCenter(), Pointing.RIGHT, 60).withItem(EGBlocks.DISPLAY_COLLECTOR.get().asItem().getDefaultInstance()).rightClick();
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
