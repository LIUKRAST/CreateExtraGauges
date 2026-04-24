package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.eg.content.logistics.board.FilterPanelBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.Gauge.*;

public class FilterGaugePonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/filter_gauge";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "filter_gauge", 9);

        scene.addInstruction(new RotateSceneInstruction(30, 100, true));
        scene.world().showIndependentSection(util.select().fromTo(1,3,8,1,1,0), Direction.DOWN);
        scene.idle(35);

        FactoryPanelPosition filterGauge = new FactoryPanelPosition(util.grid().at(2,2,5), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        FactoryPanelPosition factoryGauge = new FactoryPanelPosition(util.grid().at(2,2,3), FactoryPanelBlock.PanelSlot.TOP_LEFT);

        var stock = util.select().fromTo(2,1,7,2,2,6);

        scene.world().showIndependentSection(util.select().fromTo(filterGauge.pos(), factoryGauge.pos()), Direction.WEST);
        scene.idle(5);
        scene.world().showIndependentSection(stock, Direction.DOWN);
        scene.idle(40);

        displayText(scene, filterGauge.pos(), 60, false);
        displayText(scene, factoryGauge.pos(), 60, false);


        displayText(scene, filterGauge.pos(), 60, true);

        scene.overlay().showControls(filterGauge.pos().getCenter().add(0, 0.5, -0.5), Pointing.DOWN, 20).withItem(AllItems.ATTRIBUTE_FILTER.asStack());
        scene.idle(40);
        displayText(scene, util.grid().at(2,1,7), 60, false);

        scene.overlay().showControls(util.grid().at(2,1,7).getCenter().add(0, 0.5, -0.5), Pointing.DOWN, 20).withItem(Items.OAK_PLANKS.getDefaultInstance());

        scene.idle(40);
        setFilterGauge(scene, filterGauge, Items.OAK_PLANKS.getDefaultInstance());
        scene.idle(30);
        displayText(scene, factoryGauge.pos(), 60, false);

        addPanelConnection(scene, factoryGauge, filterGauge);

        scene.idle(30);
        displayText(scene, filterGauge.pos(), 60, true);

        scene.overlay().showControls(util.grid().at(2,1,7).getCenter().add(0, 0.5, -0.5), Pointing.DOWN, 20).withItem(Items.SPRUCE_PLANKS.getDefaultInstance());
        scene.idle(40);
        setFilterGauge(scene, filterGauge, Items.SPRUCE_PLANKS.getDefaultInstance());

        displayText(scene, filterGauge.pos(), 60, false);
        displayText(scene, filterGauge.pos().north(), 60, false);
        scene.idle(50);

    }

    public static void setFilterGauge(SceneBuilder scene, FactoryPanelPosition filterGauge, ItemStack item) {
        withGaugeDo(scene, filterGauge, gauge -> {
            if(gauge instanceof FilterPanelBehaviour f) {
                f.setFilter(AllItems.ATTRIBUTE_FILTER.asStack());
                f.ponder$setFilter(item);
            }
        });
    }
}
