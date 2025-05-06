package net.liukrast.eg.content.block.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.liukrast.eg.content.EGIcons;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class LogicGaugeRenderer extends SmartBlockEntityRenderer<LogicGaugeBlockEntity> {
    public LogicGaugeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LogicGaugeBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        var behaviour = blockEntity.behaviour;
        BlockState blockState = behaviour.blockEntity.getBlockState();
        ms.pushPose();
        Direction facing = blockState.getValue(RedstoneLinkBlock.FACING);
        float yRot = facing.getAxis()
                .isVertical() ? 0 : AngleHelper.horizontalAngle(facing) + 180;
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        ms.scale(0.25f, 0.25f, 0.25f);
        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(xRot);
        behaviour.get().getIcon().render(ms, buffer, -1);
        ms.popPose();

        for(FactoryPanelConnection connection : behaviour.targetedByLinks.values())
            renderPath(behaviour, connection, partialTicks, ms, buffer, light, overlay);
    }

    public static void renderPath(LogicGaugeBehaviour behaviour, FactoryPanelConnection connection, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = behaviour.blockEntity.getBlockState();
        var facing = blockState.getValue(LogicGaugeBlock.FACING);
        List<Direction> path = connection.getPath(behaviour.getWorld(), blockState, behaviour.getPanelPosition());

        float xRot = FactoryPanelBlock.getXRot(blockState) + Mth.PI / 2;
        float yRot = FactoryPanelBlock.getYRot(blockState);
        boolean powered = blockState.getValue(LogicGaugeBlock.POWERED);
        float glow = powered ? 0 : 1;

        FactoryPanelSupportBehaviour sbe = FactoryPanelBehaviour.linkAt(behaviour.getWorld(), connection);
        boolean displayLinkMode = sbe != null && sbe.blockEntity instanceof DisplayLinkBlockEntity;
        boolean pathReversed = sbe != null && !sbe.isOutput();

        int color = 0;
        float yOffset = 0;
        boolean success = connection.success;
        boolean dots = false;

        if (displayLinkMode) {
            // Display status
            color = 0x3C9852;
            dots = true;

        } else {
            // Link status
            color = (powered ? 0xEF0000 : 0x580101);
            yOffset = 0.5f;
        }

        float currentX = 0;
        float currentZ = 0;

        for (int i = 0; i < path.size(); i++) {
            Direction direction = path.get(i);

            if (!pathReversed) {
                currentX += direction.getStepX() * .5;
                currentZ += direction.getStepZ() * .5;
            }

            boolean isArrowSegment = pathReversed ? i == path.size() - 1 : i == 0;
            PartialModel partial = (dots ? AllPartialModels.FACTORY_PANEL_DOTTED
                    : isArrowSegment ? AllPartialModels.FACTORY_PANEL_ARROWS : AllPartialModels.FACTORY_PANEL_LINES)
                    .get(pathReversed ? direction : direction.getOpposite());
            SuperByteBuffer connectionSprite = CachedBuffers.partial(partial, blockState)
                    .rotateCentered(yRot, Direction.UP)
                    .rotateCentered(xRot, Direction.EAST)
                    .rotateCentered(Mth.PI, Direction.UP)
                    .translate(0.5 * .5 + .25, 0, 0.5 * .5 + .25)
                    .translate(currentX, (yOffset + (direction.get2DDataValue() % 2) * 0.125f) / 512f, currentZ);

            if (!displayLinkMode && !powered && !powered)
                connectionSprite.shiftUV(AllSpriteShifts.FACTORY_PANEL_CONNECTIONS);

            connectionSprite.color(color)
                    .light(light)
                    .overlay(overlay)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

            if (pathReversed) {
                currentX += direction.getStepX() * .5;
                currentZ += direction.getStepZ() * .5;
            }
        }
    }
}
