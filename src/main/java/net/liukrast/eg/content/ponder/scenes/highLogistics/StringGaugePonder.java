package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class StringGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/string_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "string_gauge", 9);

        scene.addInstruction(new RotateSceneInstruction(30, 100, true));
        scene.world().showIndependentSection(util.select().fromTo(1,3,8,1,1,0), Direction.DOWN);
        scene.idle(35);

        var gauge = new FactoryPanelPosition(util.grid().at(2,2,4), FactoryPanelBlock.PanelSlot.TOP_LEFT);
        var sign = util.grid().at(2,2,7);
        var display = util.grid().at(2,3,2);
        var nixie = util.select().fromTo(1,4,7,1,4,1);
        scene.world().showIndependentSection(util.select().position(gauge.pos()), Direction.WEST);
        scene.idle(40);

        displayText(scene, gauge.pos(), 60, false);


        scene.world().showIndependentSection(util.select().position(sign), Direction.WEST);
        scene.idle(5);
        scene.world().showIndependentSection(util.select().position(display), Direction.WEST);
        scene.idle(5);
        scene.world().showIndependentSection(nixie, Direction.DOWN);
        scene.idle(30);

        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, sign, 60, false);

        addPanelConnection(scene, gauge, new FactoryPanelPosition(sign, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        scene.idle(20);

        addPanelConnection(scene, gauge, new FactoryPanelPosition(display, FactoryPanelBlock.PanelSlot.TOP_RIGHT));
        scene.idle(20);
        scene.addKeyframe();
        scene.idle(20);
        setSignText(scene, sign,0, Component.literal("Hello!"));
        scene.overlay().showControls(sign.getCenter(), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        scene.idle(10);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("Hello!"), 7, Direction.NORTH);
        scene.idle(50);

        var gaugeA = new FactoryPanelPosition(util.grid().at(2,3,6), FactoryPanelBlock.PanelSlot.BOTTOM_LEFT);
        var gaugeB = new FactoryPanelPosition(util.grid().at(2,1,6), FactoryPanelBlock.PanelSlot.TOP_LEFT);

        var inGauges = scene.world().showIndependentSection(util.select().fromTo(gaugeA.pos(), gaugeB.pos()), Direction.WEST);
        scene.idle(50);

        displayText(scene, gaugeA.pos(), 60, true);

        addPanelConnection(scene, gauge, gaugeA);
        setArrowMode(scene, gauge, gaugeA, 1);
        scene.idle(20);
        addPanelConnection(scene, gauge, gaugeB);
        setArrowMode(scene, gauge, gaugeB, 1);
        scene.idle(20);

        displayText(scene, gaugeB.pos(), 40, false);
        displayText(scene, gaugeA.pos(), 40, false);
        scene.idle(10);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("Hello!✖ False0"), 7, Direction.NORTH);
        scene.idle(50);

        displayText(scene, gauge.pos(), 60, true);

        scene.overlay().showControls(gauge.pos().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("&")));
        scene.idle(40);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("Hello!&✖ False&0"), 7, Direction.NORTH);
        scene.idle(40);

        displayText(scene, display, 60, false);

        displayText(scene, gauge.pos(), 60, true);
        scene.overlay().showControls(gauge.pos().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("!").withStyle(ChatFormatting.RED)));
        scene.idle(40);
        scene.overlay().showControls(gauge.pos().getCenter(), Pointing.DOWN, 20).showing(createComponent(Component.literal("?").withStyle(ChatFormatting.BLUE)));
        scene.idle(40);

        displayText(scene, gauge.pos(), 60, false);
        displayText(scene, gauge.pos(), 60, false);

        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("Hello?&✖ False&0"), 7, Direction.NORTH);
        scene.idle(40);
        removePanelConnections(scene, gaugeA);
        scene.idle(5);
        removePanelConnections(scene, gaugeB);
        scene.idle(5);
        scene.world().hideIndependentSection(inGauges, Direction.EAST);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("Hello?"), 7, Direction.NORTH);
        scene.idle(10);

        scene.addKeyframe();

        var gaugeC = new FactoryPanelPosition(util.grid().at(2,3,3), FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT);
        var gaugeD = new FactoryPanelPosition(util.grid().at(2,1,3), FactoryPanelBlock.PanelSlot.TOP_RIGHT);

        scene.world().showIndependentSection(util.select().fromTo(gaugeC.pos(), gaugeD.pos()), Direction.WEST);
        scene.idle(30);

        addPanelConnection(scene, gaugeC, gauge);
        scene.idle(5);
        addPanelConnection(scene, gaugeD, gauge);
        scene.idle(30);

        displayText(scene, gaugeD.pos(), 60, false);
        displayText(scene, gaugeC.pos(), 60, false);

        setSignText(scene, sign,0, Component.literal("129"));
        scene.idle(10);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("129"), 7, Direction.NORTH);

        scene.overlay().showControls(gaugeC.pos().getCenter().add(0, 0, -0.5), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);

        scene.idle(40);
        displayText(scene, gaugeD.pos(), 60, false);

        setSignText(scene, sign,0, Component.literal("True"));
        scene.idle(10);
        scene.world().flashDisplayLink(display);
        setNixieTubeText(scene, util.grid().at(1,4,7), Component.literal("True"), 7, Direction.NORTH);

        scene.overlay().showControls(gaugeC.pos().getCenter().add(0, 0, -0.5), Pointing.DOWN, 20).showing(AllIcons.I_ACTIVE);
        setPanelPowered(scene, gaugeD, false);
        withGaugeDo(scene, gauge, behaviour -> {
            if(behaviour instanceof StringPanelBehaviour spb) {
                spb.value = "true";
            }
        });
        scene.idle(50);
    }
}
