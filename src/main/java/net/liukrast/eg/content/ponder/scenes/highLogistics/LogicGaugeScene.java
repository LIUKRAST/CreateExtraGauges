package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.content.logistics.board.ComparatorPanelBehaviour;
import net.liukrast.eg.content.logistics.board.LogicPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class LogicGaugeScene {

    public static void logicGauge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("logic_gauge", "Compact Logical Instructions with Logic Gauges");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(5, 1, 3, 1, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 4, 2, 1, 2, 4), Direction.DOWN);
        scene.idle(25);
        var logic = util.grid().at(3, 3, 2);
        var lever1 = util.grid().at(5, 4, 2);
        var link1_1 = util.grid().at(4, 4, 2);
        var link1_2 = util.grid().at(5, 4, 4);
        var lever2 = util.grid().at(5, 3, 2);
        var link2_1 = util.grid().at(4, 3, 2);
        var link2_2 = util.grid().at(5, 3, 4);
        var lever3 = util.grid().at(5, 2, 2);
        var link3_1 = util.grid().at(4, 2, 2);
        var link3_2 = util.grid().at(5, 2, 4);
        var link4_1 = util.grid().at(1, 3, 2);
        var link4_2 = util.grid().at(1, 4, 4);
        var redLamp = util.grid().at(1, 4, 3);
        scene.overlay()
                .showText(60)
                .text("Logic Gauges can read redstone information from panel elements...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link1_1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(70);

        scene.world().toggleRedstonePower(util.select().position(lever1));
        scene.effects().indicateRedstone(lever1);
        scene.world().toggleRedstonePower(util.select().position(link1_1));
        scene.world().toggleRedstonePower(util.select().position(link1_2));
        builder.world().modifyBlockEntity(link1_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link1_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.idle(40);

        scene.overlay()
                .showText(60)
                .text("And transmit it over to other panel elements")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(link4_1.getCenter().add(-0.25f, 0.25f,0));
        scene.idle(70);

        builder.world().modifyBlockEntity(logic, FactoryPanelBlockEntity.class, be -> {
            ((LogicPanelBehaviour) be.panels.get(PanelSlot.TOP_RIGHT)).power = true;
        });

        scene.world().toggleRedstonePower(util.select().position(link4_1));
        scene.world().toggleRedstonePower(util.select().position(link4_2));
        builder.world().modifyBlockEntity(link4_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link4_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.effects().indicateRedstone(redLamp);
        scene.idle(40);

        scene.overlay()
                .showControls(logic.getCenter().add(-0.5f, 0.5f, 0), Pointing.DOWN, 60)
                .rightClick();
        scene.overlay()
                .showText(60)
                .text("Connections between elements can be created via the + button in the GUI by right-clicking the gauge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(logic.getCenter().add(-0.5f, 0.55f, 0));
        scene.idle(70);

        scene.idle(10);

        scene.overlay()
                .showControls(logic.getCenter().add(-0.5f, 0.5f, 0), Pointing.DOWN, 100)
                .rightClick();
        scene.overlay()
                .showText(100)
                .text("Logic operations (OR, AND, NOR, NAND, XOR, XNOR) can be changed by holding right-click")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(logic.getCenter().add(-0.5f, 0.55f, 0));
        scene.idle(110);

        scene.idle(20);

        builder.world().modifyBlockEntity(logic, FactoryPanelBlockEntity.class, be -> {
            ((LogicPanelBehaviour) be.panels.get(PanelSlot.TOP_RIGHT)).power = false;
        });
        scene.world().toggleRedstonePower(util.select().position(link4_1));
        scene.world().toggleRedstonePower(util.select().position(link4_2));
        builder.world().modifyBlockEntity(link4_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(0));
        builder.world().modifyBlockEntity(link4_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(0));
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.idle(40);

        scene.overlay()
                .showText(70)
                .text("The logic gate, now in AND mode, will output redstone only if all links are active")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(logic.getCenter().add(-0.5f, 0.55f, 0));
        scene.idle(80);

        scene.idle(20);

        scene.world().toggleRedstonePower(util.select().position(lever2));
        scene.effects().indicateRedstone(lever2);
        scene.world().toggleRedstonePower(util.select().position(link2_1));
        scene.world().toggleRedstonePower(util.select().position(link2_2));
        builder.world().modifyBlockEntity(link2_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link2_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(lever3));
        scene.effects().indicateRedstone(lever3);
        scene.world().toggleRedstonePower(util.select().position(link3_1));
        scene.world().toggleRedstonePower(util.select().position(link3_2));
        builder.world().modifyBlockEntity(link3_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link3_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.idle(10);

        builder.world().modifyBlockEntity(logic, FactoryPanelBlockEntity.class, be -> {
            ((LogicPanelBehaviour) be.panels.get(PanelSlot.TOP_RIGHT)).power = true;
        });
        scene.world().toggleRedstonePower(util.select().position(link4_1));
        scene.world().toggleRedstonePower(util.select().position(link4_2));
        builder.world().modifyBlockEntity(link4_1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        builder.world().modifyBlockEntity(link4_2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.world().toggleRedstonePower(util.select().position(redLamp));
        scene.effects().indicateRedstone(redLamp);
        scene.idle(60);
    }
}
