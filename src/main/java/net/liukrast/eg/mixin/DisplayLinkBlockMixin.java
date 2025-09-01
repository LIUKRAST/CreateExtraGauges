package net.liukrast.eg.mixin;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import net.liukrast.eg.mixinExtension.DCFinder;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(DisplayLinkBlock.class)
public class DisplayLinkBlockMixin {

    @Inject(method = "forEachAttachedGatherer", at = @At("HEAD"))
    private static void forEachAttachedGatherer(LevelAccessor level, BlockPos pos, Consumer<DisplayLinkBlockEntity> callback, CallbackInfo ci) {
        var be = level.getBlockEntity(pos);
        if(!(be instanceof DCFinder finder)) return;
        for (BlockPos offsetPos : finder.extra_gauges$targetingDisplayCollectors()) {
            BlockState blockState = level.getBlockState(offsetPos);
            if (!blockState.is(EGBlocks.DISPLAY_COLLECTOR.get()))
                continue;

            BlockEntity blockEntity = level.getBlockEntity(offsetPos);
            if (!(blockEntity instanceof DisplayLinkBlockEntity dlbe))
                continue;
            if (dlbe.activeSource == null)
                continue;

            callback.accept(dlbe);
        }
    }
}
