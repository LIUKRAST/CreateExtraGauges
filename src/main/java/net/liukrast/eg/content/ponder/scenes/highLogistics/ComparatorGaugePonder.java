package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class ComparatorGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/comparator_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "comparator_gauge", 9);
        scene.addInstruction(new RotateSceneInstruction(30, 100, true));

        scene.world().showIndependentSection(util.select().fromTo(1,1,8,1,3,0), Direction.DOWN);

        scene.world().showIndependentSection(util.select().fromTo(2,1, 7, 2,2,1), Direction.WEST);

        scene.idle(50);

        var gauge = new FactoryPanelPosition(util.grid().at(2,2,4), FactoryPanelBlock.PanelSlot.TOP_LEFT);

        var in = new FactoryPanelPosition(util.grid().at(2,3,7), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var analog = util.grid().at(2,1,7);
        var out = util.select().fromTo(2,2,1, 1,3,1);

        displayText(scene, gauge.pos().south(), 60, false);

        displayText(scene, gauge.pos(), 80, false);

        scene.addKeyframe();

        scene.overlay().showControls(gauge.pos().getCenter().add(0, 0.5, 0), Pointing.DOWN, 20).showing(AllIcons.I_MTD_RIGHT);
        scene.idle(40);
        scene.overlay().showControls(gauge.pos().getCenter().add(0, 0.5, 0), Pointing.DOWN, 20).showing(createComponent(Component.literal("5")));
        scene.idle(40);
        displayText(scene, gauge.pos(), 80, false);

        setAnalogLever(scene, util.select().position(analog), 6);
        scene.effects().indicateRedstone(analog);

        scene.overlay().showControls(analog.getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("6")));
        scene.idle(20);
        setPanelPowered(builder, gauge, false);
        scene.world().toggleRedstonePower(out);

        displayText(scene, util.grid().at(2,2,1), 60, false);

        scene.idle(50);
        scene.addKeyframe();
        scene.world().showIndependentSection(util.select().position(in.pos()), Direction.WEST);
        scene.idle(20);
        addPanelConnection(scene, gauge, in);
        setArrowMode(scene, gauge, in, 2);
        scene.idle(20);
        displayText(scene, gauge.pos(), 60, false);


        displayText(scene, in.pos().north(), 60, false);

        scene.overlay().showControls(analog.above().north().getCenter().add(0, 0.5, 0), Pointing.DOWN, 20).showing(createComponent(Component.literal("a")));
        scene.idle(40);
        scene.overlay().showControls(in.pos().north().getCenter().add(0, 0.5, 0), Pointing.DOWN, 20).showing(createComponent(Component.literal("b")));
        scene.idle(40);

        displayText(scene, gauge.pos().south(), 60, false);

        displayText(scene, gauge.pos(), 60, true);
        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, gauge.pos(), 40, false);

        setPanelPowered(builder, gauge, true);
        scene.world().toggleRedstonePower(out);
        scene.idle(40);

        scene.overlay().showControls(in.pos().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("12")));
        scene.idle(40);


        setPanelPowered(builder, gauge, false);
        scene.world().toggleRedstonePower(out);
        scene.idle(50);


    }
}
