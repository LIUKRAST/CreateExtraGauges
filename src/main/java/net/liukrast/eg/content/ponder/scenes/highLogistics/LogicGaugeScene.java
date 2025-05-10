package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.content.logistics.logicBoard.LogicPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class LogicGaugeScene {

    public static void logicGauge(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("logic_gauge", "Compact Logical Instructions with Logic Gauges");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-0.5f);
        scene.world().showIndependentSection(util.select().fromTo(8,0, 0, 0, 0, 8), Direction.UP);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(7, 1, 4, 2, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(7, 2, 3, 2, 5, 5), Direction.DOWN);
        scene.idle(25);
        var gaugePos = util.grid().at(4, 3, 3);
        var lever1 = util.grid().at(7, 4, 3);
        var link1 = util.grid().at(6, 4, 3);
        var lever2 = util.grid().at(7, 3, 3);
        var link2 = util.grid().at(6, 3, 3);
        var lever3 = util.grid().at(7, 2, 3);
        var link3 = util.grid().at(6, 2, 3);
        var outLink = util.grid().at(2, 3, 3);
        scene.overlay()
                .showText(60)
                .text("Logic Gauges can read redstone information of panel elements...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(lever1.getCenter());
        scene.idle(50);

        //scene.world().cycleBlockProperty(new BlockPos(7, 4, 3), LeverBlock.POWERED);
        scene.world().toggleRedstonePower(util.select().position(lever1));
        scene.world().toggleRedstonePower(util.select().position(7, 4, 5));
        scene.world().toggleRedstonePower(util.select().position(link1));
        scene.world().toggleRedstonePower(util.select().fromTo(2, 3, 3, 2, 4, 5));
        withGaugeDo(builder, gaugePos, PanelSlot.TOP_LEFT, be -> {
            be.redstonePowered = true;
            be.satisfied = true;
        });
        builder.world().modifyBlockEntity(link1, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.effects().indicateRedstone(lever1);
        scene.effects().indicateRedstone(link1);
        scene.idle(40);

        scene.overlay()
                .showText(70)
                .text("And transmit it over to other panel elements")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(outLink.getCenter());
        scene.idle(120);


        scene.overlay()
                        .showControls(gaugePos.getCenter().add(0, 0.5f, 0), Pointing.DOWN, 50)
                                .rightClick();
        scene.overlay()
                .showText(90)
                .text("Reaction to multiple information can be changed by holding right-click")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(0, 0.5f, 0));
        withGaugeDo(builder, gaugePos, PanelSlot.TOP_LEFT, be -> ((LogicPanelBehaviour)be).setValue(1));
        scene.world().toggleRedstonePower(util.select().fromTo(2, 3, 3, 2, 4, 5));
        scene.idle(120);
        scene.overlay()
                .showText(70)
                .text("The logic gate will now output redstone only if all links are active")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(0, 0.5f, 0));
        scene.idle(60);
        scene.world().toggleRedstonePower(util.select().position(lever2));
        scene.world().toggleRedstonePower(util.select().position(link2));
        builder.world().modifyBlockEntity(link2, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.effects().indicateRedstone(lever2);
        scene.effects().indicateRedstone(link2);
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(lever3));
        scene.world().toggleRedstonePower(util.select().position(link3));
        builder.world().modifyBlockEntity(link3, RedstoneLinkBlockEntity.class, be -> be.setSignal(15));
        scene.effects().indicateRedstone(lever3);
        scene.effects().indicateRedstone(link3);
        withGaugeDo(builder, gaugePos, PanelSlot.TOP_LEFT, be -> {
            be.redstonePowered = true;
            be.satisfied = true;
        });
        scene.world().toggleRedstonePower(util.select().fromTo(2, 3, 3, 2, 4, 5));
        scene.idle(60);
        scene.world().hideSection(util.select().fromTo(2, 1, 3, 7, 5, 5), Direction.NORTH);
        //scene.world().moveSection(scene.world().makeSectionIndependent(util.select().fromTo(2, 1, 3, 7, 5, 5)), new Vec3(0, 0, -20), 30);

        scene.idle(20);
        ElementLink<WorldSectionElement> panel2 = scene.world().showIndependentSection(util.select().fromTo(2, 1, 6, 7, 4, 7), Direction.NORTH);
        scene.world().moveSection(panel2, util.vector().of(0, 0, -3), 0);
        scene.idle(10);

        scene.overlay()
                .showText(60)
                .text("Logic panels can interact with factory gauges as well")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(gaugePos.getCenter().add(-.5f, 0, 0));
        scene.idle(70);
    }

    private static void withGaugeDo(SceneBuilder builder, BlockPos gauge, @SuppressWarnings("SameParameterValue") PanelSlot slot,
                                    Consumer<FactoryPanelBehaviour> call) {
        builder.world()
                .modifyBlockEntity(gauge, FactoryPanelBlockEntity.class, be -> call.accept(be.panels.get(slot)));
    }
}
