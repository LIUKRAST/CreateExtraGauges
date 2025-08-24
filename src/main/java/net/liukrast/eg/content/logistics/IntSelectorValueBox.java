package net.liukrast.eg.content.logistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class IntSelectorValueBox extends ValueBoxTransform {

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Vec3 vec = new Vec3(.5, 1.5/16f, .5);
        vec = VecHelper.rotateCentered(vec, 180, Direction.Axis.Y);
        vec = VecHelper.rotateCentered(vec, Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(state) + 90, Direction.Axis.X);
        vec = VecHelper.rotateCentered(vec, Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(state), Direction.Axis.Y);
        return vec;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        TransformStack.of(ms)
                .rotate(FactoryPanelBlock.getYRot(state) + Mth.PI, Direction.UP)
                .rotate(-FactoryPanelBlock.getXRot(state), Direction.EAST);
    }
}
