package net.liukrast.eg.content.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.eg.api.util.IFPExtra;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.function.Consumer;

public class GaugeHelper {
    public static CreateSceneBuilder simpleInit(SceneBuilder builder, SceneBuildingUtil util, String id) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title(id, "");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.825f);
        scene.setSceneOffsetY(-2f);
        scene.world().showIndependentSection(util.select().fromTo(7, 0, 0, 0, 0, 7), Direction.UP);
        scene.idle(10);
        return scene;
    }

    public static void displayText(SceneBuilder builder, BlockPos pos, int time, boolean keyframe) {
        var overlay = builder.overlay()
                .showText(time)
                .text("")
                .placeNearTarget()
                .pointAt(pos.getCenter().add(-0.25f, 0.25f,0));
        if(keyframe) overlay.attachKeyFrame();
        builder.idle(time+20);
    }

    public static void activateRedstone(SceneBuilder builder, BlockPos pos) {
        builder.world().toggleRedstonePower(builder.getScene().getSceneBuildingUtil().select().position(pos));
        builder.effects().indicateRedstone(pos);
    }

    public static void setSignText(SceneBuilder builder, BlockPos pos, int line, Component text) {
        builder.world().modifyBlockEntity(pos, SignBlockEntity.class, be -> be.setText(be.getText(true).setMessage(line, text), true));
    }

    public static void setNixieTubeText(SceneBuilder builder, BlockPos start, Component text, int length, Direction direction) {
        for(int i = 0; i < length; i++) {
            final int index = i;
            builder.world().modifyBlockEntityNBT(builder.getScene().getSceneBuildingUtil().select().position(start.relative(direction, index)), NixieTubeBlockEntity.class, nbt -> {
                String asRaw = Component.Serializer.toJson(text);
                nbt.putString("RawCustomText", asRaw);
                nbt.putString("CustomText", asRaw);
                nbt.putInt("CustomTextIndex", index);
            });
        }
    }

    public static void setPanelItem(SceneBuilder builder, FactoryPanelPosition gauge, ItemStack item) {
        withGaugeDo(builder, gauge, pb -> pb.setFilter(item));
    }

    public static void setPanelPowered(SceneBuilder builder, FactoryPanelPosition gauge, boolean power) {
        withGaugeDo(builder, gauge, pb -> pb.redstonePowered = power);
    }

    @SuppressWarnings("unused")
    public static void setPanelVisible(SceneBuilder builder, FactoryPanelPosition gauge, boolean visible) {
        withGaugeDo(builder, gauge, pb -> pb.active = visible);
    }

    public static void setPanelNotSatisfied(SceneBuilder builder, FactoryPanelPosition gauge) {
        withGaugeDo(builder, gauge, pb -> pb.count = 2);
    }

    public static void setPanelCrafting(SceneBuilder builder, SceneBuildingUtil util, FactoryPanelPosition gauge) {
        builder.world().modifyBlockEntityNBT(util.select().position(gauge.pos()), FactoryPanelBlockEntity.class, tag -> {
            CompoundTag panelTag = tag.getCompound(CreateLang.asId(gauge.slot().name()));
            panelTag.putInt("LastPromised", 1);
        });
    }

    @SuppressWarnings("unused")
    public static void flash(SceneBuilder builder, FactoryPanelPosition gauge) {
        withGaugeDo(builder, gauge, pb -> pb.bulb.setValue(1));
    }

    public static void setPanelSatisfied(SceneBuilder builder, FactoryPanelPosition gauge) {
        withGaugeDo(builder, gauge, pb -> pb.count = 1);
    }

    public static void setPanelPassive(SceneBuilder builder, FactoryPanelPosition gauge) {
        withGaugeDo(builder, gauge, pb -> pb.count = 0);
    }

    public static void setConnectionAmount(SceneBuilder builder, FactoryPanelPosition from, FactoryPanelPosition to, int amount) {
        withGaugeDo(builder, from, pb -> pb.targetedBy.get(to).amount = amount);
    }

    public static void removePanelConnections(SceneBuilder builder, FactoryPanelPosition gauge) {
        withGaugeDo(builder, gauge, FactoryPanelBehaviour::disconnectAll);
    }

    public static void setArrowMode(SceneBuilder builder, FactoryPanelPosition gauge, FactoryPanelPosition from, int mode) {
        withGaugeDo(builder, gauge, pb -> {
            FactoryPanelConnection connection = pb.targetedBy.get(new FactoryPanelPosition(from.pos(), from.slot()));
            if (connection == null) {
                connection = pb.targetedByLinks.get(from.pos());
                if (connection == null) {
                    connection = ((IFPExtra)pb).extra_gauges$getExtra().get(from.pos());
                    if(connection == null) return;
                }
            }
            connection.arrowBendMode = mode;
        });
    }

    public static void addPanelConnection(SceneBuilder builder, FactoryPanelPosition gauge, FactoryPanelPosition from) {
        withGaugeDo(builder, gauge, pb -> pb.addConnection(new FactoryPanelPosition(from.pos(), from.slot())));
    }

    public static void withGaugeDo(SceneBuilder builder, FactoryPanelPosition gauge,
                                    Consumer<FactoryPanelBehaviour> call) {
        builder.world()
                .modifyBlockEntity(gauge.pos(), FactoryPanelBlockEntity.class, be -> call.accept(be.panels.get(gauge.slot())));
    }

}
