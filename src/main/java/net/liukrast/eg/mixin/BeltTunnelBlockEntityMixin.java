package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.util.DCFinder;
import net.liukrast.eg.content.logistics.DisplayCollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(BeltTunnelBlockEntity.class)
public abstract class BeltTunnelBlockEntityMixin extends BlockEntity implements DCFinder {

    @Unique private final Set<BlockPos> extra_gauges$targetingDisplayCollectors = new HashSet<>();

    public BeltTunnelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override public Set<BlockPos> extra_gauges$targetingDisplayCollectors() {return extra_gauges$targetingDisplayCollectors;}

    @Inject(method = "write", at = @At("HEAD"))
    private void writeSafe(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        ListTag list = new ListTag();
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        for(BlockPos pos : extra_gauges$targetingDisplayCollectors) {
            BlockPos.CODEC.encodeStart(ops, pos)
                    .resultOrPartial(EGConstants.LOGGER::error)
                    .ifPresent(list::add);
        }
        tag.put("extra_gauges$targetingDisplayCollectors", list);
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if(!tag.contains("extra_gauges$targetingDisplayCollectors")) return;
        ListTag list = tag.getList("extra_gauges$targetingDisplayCollectors", Tag.TAG_END); //TODO: CHECK
        extra_gauges$targetingDisplayCollectors.clear();
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        for(Tag tag1 : list) {
            BlockPos.CODEC
                    .parse(ops, tag1)
                    .resultOrPartial(EGConstants.LOGGER::error)
                    .ifPresent(pos -> {
                        var level = getLevel();
                        if(level == null || !level.isLoaded(pos)) {
                            extra_gauges$targetingDisplayCollectors.add(pos);
                            return;
                        }
                        if(!(level.getBlockEntity(pos) instanceof DisplayCollectorBlockEntity)) return;
                        extra_gauges$targetingDisplayCollectors.add(pos);
                    });
        }
    }
}
