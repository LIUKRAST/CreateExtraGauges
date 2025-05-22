package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class IntGaugeScene {

    public static void intGaugeRedstone(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("integer_gauge_redstone", "Compact Logical Instructions with Integer Gauges");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(6,0, 0, 0, 0, 6), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(5, 1, 3, 1, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 5, 4, 1, 2, 2), Direction.DOWN);
        scene.idle(25);

        var intGauge1 = util.grid().at(4, 2, 2);
        var lever1 = util.grid().at(5, 4, 2);
        var nixie1 = util.grid().at(5, 5, 3);
        var link1_1 = util.grid().at(5,3,2);
        var link1_2 = util.grid().at(5,4,4);
        var lever2 = util.grid().at(4, 4, 2);
        var nixie2 = util.grid().at(4, 5, 3);
        var link2_1 = util.grid().at(4,3,2);
        var link2_2 = util.grid().at(4,4,4);
        var outLink = util.grid().at(1,4,2);
        var nixie3 = util.grid().at(1,5,3);

        scene.overlay()
                .showText(40)
                .text("Integer Gauges can read integer information from panel elements...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link2_1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(50);

        scene.world().modifyBlockEntityNBT(util.select().position(lever1), AnalogLeverBlockEntity.class, nbt -> {
            nbt.putInt("State", 7);
        });
        scene.effects().indicateRedstone(lever1);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        scene.world().modifyBlockEntityNBT(util.select().position(nixie1), NixieTubeBlockEntity.class, nbt -> {
            nbt.putInt("RedstoneStrength", 7);
        });
        scene.world().toggleRedstonePower(util.select().position(lever2));
        scene.effects().indicateRedstone(lever2);
        scene.world().toggleRedstonePower(util.select().position(link2_1));
        scene.world().toggleRedstonePower(util.select().position(link2_2));
        scene.world().modifyBlockEntityNBT(util.select().position(nixie2), NixieTubeBlockEntity.class, nbt -> {
            nbt.putInt("RedstoneStrength", 15);
        });

        builder.world().modifyBlockEntity(link1_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(7));
        builder.world().modifyBlockEntity(link1_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(7));
        builder.world().modifyBlockEntity(link2_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link2_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.idle(40);

        scene.overlay()
                .showText(40)
                .text("And transmit it over to other panel elements, after summing them up")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(outLink.getCenter());
        scene.idle(60);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie3), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("21");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });

        scene.overlay()
                .showControls(intGauge1.getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 40)
                .rightClick();
        scene.overlay()
                .showText(40)
                .text("Connections between elements can be created via the + button in the GUI by right-clicking the gauge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(intGauge1.getCenter().add(-0.5f, 0, 0));
        scene.idle(60);

        scene.overlay()
                .showControls(intGauge1.getCenter().add(-0.5f, 0, 0), Pointing.DOWN, 40)
                .rightClick();
        scene.overlay()
                .showText(40)
                .text("Logic operations (+, -, x) can be changed by holding right-click")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(intGauge1.getCenter().add(-0.5f, 0, 0));
        scene.idle(60);
    }

    public static void intGaugeFactory(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("integer_gauge_factory", "Compact Logical Instructions with Integer Gauges");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(4,0, 0, 0, 0, 4), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(3, 1, 2, 1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 5, 2, 1, 2, 0), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(3, 1, 1, 1, 1, 1), Direction.DOWN);
        scene.idle(25);

        var intGauge = util.grid().at(2,2,1);
        var factGauge1 = util.grid().at(3,4,1);
        var factGauge2 = util.grid().at(3,2,1);
        var outLink = util.grid().at(1,4,1);
        var nixie = util.grid().at(1,5,2);
        var chest = util.grid().at(1,1,1);

        scene.overlay()
                .showText(50)
                .text("Integer Gauges can read integer information from Factory Gauges...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(factGauge1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(60);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("96");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });

        scene.overlay()
                .showText(50)
                .text("And transmit it over to other panel elements, after summing them up")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(outLink.getCenter());
        scene.idle(60);

        scene.overlay()
                .showControls(chest.getCenter(), Pointing.DOWN, 50)
                .withItem(Items.DIAMOND.getDefaultInstance());
        scene.idle(70);

        scene.world().flashDisplayLink(outLink);
        scene.world().modifyBlockEntityNBT(util.select().position(nixie), NixieTubeBlockEntity.class, nbt -> {
            Component text = Component.literal("97");
            nbt.putString("RawCustomText", text.getString());
            nbt.putString("CustomText", Component.Serializer.toJson(text, scene.world().getHolderLookupProvider()));
        });
    }
}
