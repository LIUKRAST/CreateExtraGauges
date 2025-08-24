package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class IntGaugeScene {

    public static void intGauge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("integer_gauge", "Using Integer Gauges to transmit numbers");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);

        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        scene.world().showIndependentSection(util.select().fromTo(5, 1, 3, 1, 5, 2), Direction.NORTH);
        scene.idle(40);

        var gaugePos = new BlockPos(3,3,2);
        var leverPos = new BlockPos(5,2,2);
        var analogPos = new BlockPos(3,2,2);
        var selectorPos = new BlockPos(1,2,2);

        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        scene.overlay()
                .showText(80)
                .text("")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(leverPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(90);
        scene.world().flashDisplayLink(new BlockPos(5,4,2));
        scene.effects().indicateRedstone(leverPos);
        scene.world().toggleRedstonePower(util.select().position(leverPos));
        scene.world().modifyBlockEntityNBT(util.select().position(new BlockPos(5,5,3)), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("15");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text));
        });
        scene.idle(20);
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .pointAt(analogPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(90);
        scene.world().modifyBlockEntity(analogPos, AnalogLeverBlockEntity.class, be -> be.changeState(true));
        scene.effects().indicateRedstone(analogPos);
        scene.world().flashDisplayLink(new BlockPos(3,4,2));
        scene.world().modifyBlockEntityNBT(util.select().position(new BlockPos(3,5,3)), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("1");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text));
        });
        scene.idle(20);
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .pointAt(selectorPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(90);
        scene.effects().indicateRedstone(selectorPos);
        scene.world().flashDisplayLink(new BlockPos(1,4,2));
        scene.world().modifyBlockEntityNBT(util.select().position(new BlockPos(1,5,3)), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("89");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text));
        });
        scene.idle(40);
        scene.overlay()
                .showText(100)
                .text("")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(selectorPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
        scene.overlay()
                .showText(80)
                .text("")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        scene.world().modifyBlockEntity(gaugePos, FactoryPanelBlockEntity.class, be -> be.panels.get(FactoryPanelBlock.PanelSlot.BOTTOM_LEFT).targetedBy.put(
                new FactoryPanelPosition(new BlockPos(5,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT),
                new FactoryPanelConnection(new FactoryPanelPosition(new BlockPos(5,3,2), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT), 1)
        ));
        scene.overlay()
                .showText(80)
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);
        scene.world().flashDisplayLink(new BlockPos(3,4,2));
        scene.world().modifyBlockEntityNBT(util.select().position(new BlockPos(3,5,3)), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("16");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text));
        });
        scene.idle(40);
        scene.overlay()
                .showControls(gaugePos.getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 100)
                .rightClick();
        scene.overlay()
                .showText(120)
                .text("")
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
    }

    public static void intGaugeStorage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("integer_gauge_storage", "");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);

        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        scene.world().showIndependentSection(util.select().fromTo(5, 1, 3, 1, 5, 2), Direction.NORTH);
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
        scene.world().flashDisplayLink(new BlockPos(5,4,2));
        scene.world().modifyBlockEntityNBT(util.select().position(new BlockPos(5,5,3)), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("16");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text));
        });
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(gaugePos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(140);
        scene.overlay()
                .showText(100)
                .text("")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(targetPos.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(120);
    }
}
