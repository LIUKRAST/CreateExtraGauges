package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import static net.liukrast.deployer.lib.util.ponder.GaugeHelper.*;

public class LogicGaugeScene {

    public static void logicGauge(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "logic_gauge");

        scene.world().showIndependentSection(util.select().fromTo(5,1,3,1,4,3), Direction.NORTH);
        scene.world().showIndependentSection(util.select().fromTo(5,3,2,1,3,2), Direction.NORTH);
        scene.idle(40);

        var gaugePos = new FactoryPanelPosition(new BlockPos(3,3,2), PanelSlot.TOP_RIGHT);
        var leverPos = new BlockPos(5,3,2);
        var leverPos2 = new FactoryPanelPosition(new BlockPos(5,4,2), PanelSlot.BOTTOM_RIGHT);
        var linkPos = new BlockPos(1,3,2);
        var dLink = new FactoryPanelPosition(new BlockPos(1,4,2), PanelSlot.TOP_LEFT);

        displayText(scene, gaugePos.pos(), 100, false);
        displayText(scene, leverPos, 100, true);
        displayText(scene, linkPos, 80, false);
        displayText(scene, leverPos, 100, true);
        activateRedstone(scene, leverPos);
        activateRedstone(scene, linkPos);
        setPanelPowered(scene, gaugePos, false);
        scene.idle(20);
        displayText(scene, gaugePos.pos(), 60, false);
        //END PHASE 1
        scene.world().toggleRedstonePower(util.select().position(leverPos));
        scene.world().toggleRedstonePower(util.select().position(linkPos));
        setPanelPowered(scene, gaugePos, true);
        //START PHASE 2
        scene.world().showIndependentSection(util.select().position(leverPos2.pos()),Direction.SOUTH);
        addPanelConnection(scene, gaugePos, leverPos2);
        setArrowMode(scene, gaugePos, leverPos2, 2);
        scene.idle(40);
        displayText(scene, gaugePos.pos(), 120, true);
        activateRedstone(scene, leverPos2.pos());
        activateRedstone(scene, linkPos);
        setPanelPowered(scene, gaugePos, false);
        scene.idle(40);
        scene.overlay().showControls(gaugePos.pos().getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 100).rightClick();
        displayText(scene, gaugePos.pos(), 100, true);
        scene.world().toggleRedstonePower(util.select().position(linkPos));
        setPanelPowered(scene, gaugePos, true);
        scene.idle(40);
        displayText(scene, gaugePos.pos(), 100, true);
        activateRedstone(scene, leverPos);
        activateRedstone(scene, linkPos);
        setPanelPowered(scene, gaugePos, false);
        scene.idle(40);
        scene.world().showIndependentSection(util.select().fromTo(4,5,3,1,5,3), Direction.DOWN);
        scene.world().showIndependentSection(util.select().position(1,4,2), Direction.SOUTH);
        scene.idle(40);
        displayText(scene, gaugePos.pos(), 100, true);
        addPanelConnection(scene, gaugePos, dLink);
        scene.idle(40);
        scene.world().flashDisplayLink(dLink.pos());
        setNixieTubeText(scene, new BlockPos(3,5,3), Component.literal("✔ True"), 3, Direction.WEST);
        scene.idle(40);
    }

    public static void logicGaugeStorage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("logic_gauge_storage", "Making Logic Gauges interact with Factory gauges");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);

        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        scene.world().showIndependentSection(util.select().fromTo(5,1,2,1,4,3), Direction.NORTH);
        scene.idle(40);

        var gaugePos = new BlockPos(2,3,2);
        var originPos = new BlockPos(4,3,2);
        var targetPos = new BlockPos(1,3,2);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(originPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        builder.world().modifyBlockEntityNBT(util.select().position(originPos), FactoryPanelBlockEntity.class, tag -> {
            CompoundTag panelTag = tag.getCompound(CreateLang.asId(PanelSlot.TOP_RIGHT.name()));
            panelTag.putBoolean("Satisfied", true);
            panelTag.putInt("FilterAmount", 64);
        });
        builder.world().modifyBlockEntity(originPos, FactoryPanelBlockEntity.class, be -> be.panels.get(PanelSlot.TOP_RIGHT).count=1);
        builder.world().modifyBlockEntity(gaugePos, FactoryPanelBlockEntity.class, be -> be.panels.get(PanelSlot.TOP_LEFT).redstonePowered = false);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        builder.world().modifyBlockEntity(targetPos, FactoryPanelBlockEntity.class, be -> (be.panels.get(PanelSlot.TOP_LEFT)).redstonePowered = true);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(targetPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
    }
}
