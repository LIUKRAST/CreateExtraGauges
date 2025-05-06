package net.liukrast.eg.content.block.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LogicGaugePanelSlotPositioning extends ValueBoxTransform {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(RedstoneLinkBlock.FACING);
        Vec3 location = VecHelper.voxelSpace(8, 2, 8);
        if(facing.getAxis().isVertical()) location = location.add(0, -0.4/16f, 0);
        if(!facing.getAxis().isVertical()) {
            boolean bl = facing.getAxis() == Direction.Axis.X;
            float f = 6.4f/16f * (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 1);
            location = location.add(bl ? f : 0, 6/16f, bl ? 0 : f);
        }
        //location = VecHelper.rotateCentered(location, facing == Direction.DOWN ? 180 : 0, Direction.Axis.X);
        return location;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = state.getValue(RedstoneLinkBlock.FACING);
        float yRot = facing.getAxis()
                .isVertical() ? 0 : AngleHelper.horizontalAngle(facing) + 180;
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(xRot);
    }

    @Override
    public float getScale() {
        return .4975f;
    }
}
