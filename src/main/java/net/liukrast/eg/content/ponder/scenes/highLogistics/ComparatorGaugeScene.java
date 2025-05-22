package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.content.logistics.board.ComparatorPanelBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;

public class ComparatorGaugeScene {

    public static void compGaugeRedstone(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("comparator_gauge_redstone", "Compact Logical Instructions with Comparator Gauges");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(6,0, 0, 0, 0, 6), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(5, 1, 3, 1, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 5, 4, 1, 2, 2), Direction.DOWN);
        scene.idle(25);

        var comparator = util.grid().at(3, 3, 2);
        var lever1 = util.grid().at(5, 4, 2);
        var link1_1 = util.grid().at(5,4,5);
        var link1_2 = util.grid().at(4,4,2);
        var lever2 = util.grid().at(5, 2, 2);
        var link2_1 = util.grid().at(5,2,4);
        var link2_2 = util.grid().at(4,2,2);
        var redLamp = util.grid().at(1,4,3);
        var link3_1 = util.grid().at(1,3,2);
        var link3_2 = util.grid().at(1,4,4);

        scene.overlay()
                .showText(40)
                .text("Comparator Gauges can read numerical information from panel elements, sum them up...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link1_2.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(50);

        scene.world().modifyBlockEntityNBT(util.select().position(lever1), AnalogLeverBlockEntity.class, nbt -> {
            nbt.putInt("State", 5);
        });
        scene.effects().indicateRedstone(lever1);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));

        scene.world().modifyBlockEntityNBT(util.select().position(lever2), AnalogLeverBlockEntity.class, nbt -> {
            nbt.putInt("State", 10);
        });
        scene.effects().indicateRedstone(lever2);
        scene.world().toggleRedstonePower(util.select().position(link2_1));
        scene.world().toggleRedstonePower(util.select().position(link2_2));

        builder.world().modifyBlockEntity(link1_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(5));
        builder.world().modifyBlockEntity(link1_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(5));
        builder.world().modifyBlockEntity(link2_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(10));
        builder.world().modifyBlockEntity(link2_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(10));
        scene.idle(40);

        scene.overlay()
                .showText(70)
                .text("And transmit the result of the comparison (in this case \"input signal = 15\") to other panel elements")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link3_1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(100);

        scene.world().toggleRedstonePower(util.select().position(link3_1));
        scene.world().toggleRedstonePower(util.select().position(link3_2));
        builder.world().modifyBlockEntity(link3_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link3_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(comparator, FactoryPanelBlockEntity.class, be -> {
            ((ComparatorPanelBehaviour)be.panels.get(FactoryPanelBlock.PanelSlot.TOP_LEFT)).power = true;
        });
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.effects().indicateRedstone(redLamp);

        scene.overlay()
                .showControls(comparator.getCenter().add(0, 0.55f, 0), Pointing.DOWN, 40)
                .rightClick();
        scene.overlay()
                .showText(40)
                .text("Connections between elements can be created via the + button in the GUI by right-clicking the gauge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(comparator.getCenter().add(0, 0.53f, 0));
        scene.idle(60);

        scene.overlay()
                .showControls(comparator.getCenter().add(0, 0.55f, 0), Pointing.DOWN, 40)
                .rightClick();
        scene.overlay()
                .showText(40)
                .text("The comparing value can be changed by holding right-click")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(comparator.getCenter().add(0, 0.53f, 0));
        scene.idle(60);
    }

    public static void compGaugeFactory(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("comparator_gauge_factory", "Compact Logical Instructions with Comparator Gauges");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(6,0, 0, 0, 0, 6), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(5, 1, 3, 1, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 5, 4, 1, 2, 2), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(5,1,2,2,1,2), Direction.DOWN);
        scene.idle(25);

        var comparator = util.grid().at(3, 3, 2);
        var factory1 = util.grid().at(5,4,2);
        var factory2 = util.grid().at(5, 3, 2);
        var link1_1 = util.grid().at(1,3,2);
        var link1_2 = util.grid().at(1,4,4);
        var redLamp = util.grid().at(1,4,3);
        var chest = util.grid().at(3,1,2);

        scene.overlay()
                .showText(50)
                .text("Comparator Gauges can read numerical information from Factory Gauges...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(factory1.getCenter().add(-0.5f, 0,0));
        scene.idle(60);

        scene.overlay()
                .showText(50)
                .text("And transmit the result of the comparison (in this case \"input signal = 96\") to other panel elements")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link1_1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(60);

        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        builder.world().modifyBlockEntity(link1_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link1_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(comparator, FactoryPanelBlockEntity.class, be -> {
            ((ComparatorPanelBehaviour)be.panels.get(FactoryPanelBlock.PanelSlot.TOP_LEFT)).power = true;
        });
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.effects().indicateRedstone(redLamp);
        scene.idle(40);

        scene.overlay()
                .showControls(chest.getCenter().add(0.2f,0.1f,0.25f), Pointing.DOWN, 70)
                .withItem(Items.DIAMOND.getDefaultInstance());
        scene.idle(50);

        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        builder.world().modifyBlockEntity(link1_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(0));
        builder.world().modifyBlockEntity(link1_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(0));
        builder.world().modifyBlockEntity(comparator, FactoryPanelBlockEntity.class, be -> {
            ((ComparatorPanelBehaviour)be.panels.get(FactoryPanelBlock.PanelSlot.TOP_LEFT)).power = false;
        });
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.effects().indicateRedstone(redLamp);
        scene.idle(30);
    }
}
