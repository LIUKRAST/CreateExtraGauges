package net.liukrast.eg.content.logistics;

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

public class LinkedLeverFrequencySlot extends ValueBoxTransform.Dual {

    public LinkedLeverFrequencySlot(boolean first) {
        super(first);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(RedstoneLinkBlock.FACING);

        if(facing.getAxis().isHorizontal()) {
            return rotateHorizontally(state, isFirst()
                    ? VecHelper.voxelSpace(6.2f, 4.5f, 3.20f)
                    : VecHelper.voxelSpace(9.8f, 4.5f, 3.20f)
            );
        }

        return VecHelper.rotateCentered(isFirst()
                ? VecHelper.voxelSpace(9.8f, 3.5f, 4.4f)
                : VecHelper.voxelSpace(6.1f, 3.5f, 4.4f)
                , facing == Direction.DOWN ? 180 : 0, Direction.Axis.X);
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
        return 6/16f;
    }
}
