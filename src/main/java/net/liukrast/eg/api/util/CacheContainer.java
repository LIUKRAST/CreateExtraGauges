package net.liukrast.eg.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.networking.PanelCacheUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;

public interface CacheContainer<T> {

    Codec<T> cacheCodec();
    Map<BlockPos, T> cacheMap();

    default void setCache(CompoundTag tag, DynamicOps<Tag> dynamicOps) {
        var cache = cacheMap();
        var codec = cacheCodec();
        cache.clear();
        for(String key : tag.getAllKeys()) {
            String[] t = key.split(",");
            try {
                BlockPos pos = new BlockPos(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
                codec.parse(dynamicOps, tag.get(key))
                        .resultOrPartial(EGConstants.LOGGER::error)
                        .ifPresent(r -> cache.put(pos, r));
            } catch (NumberFormatException e) {
                EGConstants.LOGGER.error("Unable to parse compound tag {}", tag.get(key), e);
            }
        }

    }

    default void getCache(CompoundTag tag, DynamicOps<Tag> dynamicOps) {
        var cache = cacheMap();
        var codec = cacheCodec();
        for(BlockPos pos : cache.keySet()) {
            String key = pos.getX()+","+pos.getY()+","+pos.getZ();
            codec.encodeStart(dynamicOps, cache.get(pos))
                    .resultOrPartial(EGConstants.LOGGER::error)
                    .ifPresent(tag1 -> tag.put(key, tag1));
        }

    }

    default void sendCache(FactoryPanelBehaviour behaviour) {
        var level = behaviour.blockEntity.getLevel();
        if(level == null || level.isClientSide) return;
        var tag = new CompoundTag();
        getCache(tag, level.registryAccess().createSerializationContext(NbtOps.INSTANCE));
        CatnipServices.NETWORK.sendToClientsTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(behaviour.blockEntity.getBlockPos()),
                new PanelCacheUpdatePacket(behaviour.getPanelPosition(), tag)
        );
    }
}
