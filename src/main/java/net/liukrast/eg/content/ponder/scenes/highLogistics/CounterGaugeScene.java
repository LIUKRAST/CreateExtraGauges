package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.content.logistics.board.CounterPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class CounterGaugeScene {

    public static void countGauge(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("counter_gauge", "Compact Logical Instructions with Counter Gauges");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(4,0, 0, 0, 0, 4), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(3, 1, 2, 1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 5, 3, 1, 2, 0), Direction.DOWN);
        scene.idle(25);

        var button = util.grid().at(3,4,1);
        var link1_1 = util.grid().at(3,3,1);
        var link1_2 = util.grid().at(3,4,3);
        var counter = util.grid().at(2,3,1);
        var outLink = util.grid().at(1,4,1);
        var nixie = util.grid().at(1,5,2);

        scene.rotateCameraY(180);
        scene.idle(40);
        scene.overlay()
                .showText(80)
                .text("Setup")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link1_2.getCenter().add(-0.25, 0.3, 0));
        scene.idle(100);
        scene.rotateCameraY(180);
        scene.idle(40);

        scene.overlay()
                .showText(60)
                .text("Counter Gauges can count the number of redstone inputs...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(button.getCenter().add(-0.25,0.3,0));
        scene.idle(80);

        scene.world().toggleRedstonePower(util.select().position(button));
        scene.effects().indicateRedstone(button);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);

        scene.overlay()
                .showText(60)
                .text("And transmit it over to other panel elements")
                .placeNearTarget()
                .pointAt(outLink.getCenter());
        scene.idle(80);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("1");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });
        scene.idle(20);

        scene.overlay()
                .showControls(counter.getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 40)
                .rightClick();
        scene.overlay()
                .showText(40)
                .text("Connections between elements can be created via the + button in the GUI by right-clicking the gauge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(counter.getCenter().add(-0.5f, 0.1, 0));
        scene.idle(60);

        scene.world().toggleRedstonePower(util.select().position(button));
        scene.effects().indicateRedstone(button);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("2");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });
        scene.idle(20);

        scene.world().toggleRedstonePower(util.select().position(button));
        scene.effects().indicateRedstone(button);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.world().modifyBlockEntity(counter, FactoryPanelBlockEntity.class, be -> {
            CounterPanelBehaviour panel = (CounterPanelBehaviour) be.panels.get(FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);
            panel.count = 3;
            panel.value = 3;
            panel.redstonePowered = false;
        });
        scene.world().toggleRedstonePower(util.select().fromTo(1,2, 1, 1, 2, 3));
        scene.effects().indicateRedstone(new BlockPos(1, 2, 1));
        scene.idle(15);
        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("3");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });
        scene.idle(20);
        scene.overlay()
                .showText(70)
                .text("")
                .placeNearTarget()
                .pointAt(new BlockPos(1, 2, 1).getCenter());
        scene.idle(100);
        scene.idle(15);

        scene.overlay()
                .showText(40)
                .text("")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(outLink.getCenter());
        scene.idle(60);

        scene.world().toggleRedstonePower(util.select().position(button));
        scene.effects().indicateRedstone(button);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.world().modifyBlockEntity(counter, FactoryPanelBlockEntity.class,be -> {
            CounterPanelBehaviour panel = (CounterPanelBehaviour) be.panels.get(FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);
            panel.count = 0;
            panel.value = 3;
            panel.redstonePowered = true;
        });
        scene.world().toggleRedstonePower(util.select().fromTo(1,2, 1, 1, 2, 3));
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().position(button));
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.idle(15);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("0");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });
        scene.idle(20);
    }
}
