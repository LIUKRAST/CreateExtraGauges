package net.liukrast.eg.networking;

import com.mojang.serialization.DynamicOps;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.util.CacheContainer;
import net.liukrast.eg.registry.RegisterPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class PanelCacheUpdatePacket extends BlockEntityDataPacket<FactoryPanelBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, PanelCacheUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            FactoryPanelPosition.STREAM_CODEC, packet -> packet.position,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet.compound,
            PanelCacheUpdatePacket::new
    );
    private final FactoryPanelPosition position;
    private final CompoundTag compound;

    public PanelCacheUpdatePacket(FactoryPanelPosition position, CompoundTag compound) {
        super(position.pos());
        this.position = position;
        this.compound = compound;
    }

    @Override
    protected void handlePacket(FactoryPanelBlockEntity blockEntity) {
        FactoryPanelBehaviour behaviour = blockEntity.panels.get(position.slot());
        if(!ExtraGaugesConfig.PANEL_CACHING.get()) return;
        if(!(behaviour instanceof CacheContainer<?> cacheContainer)) return;
        if(Minecraft.getInstance().level == null) return;
        var access = Minecraft.getInstance().level.registryAccess();
        DynamicOps<Tag> dynamicOps = access.createSerializationContext(NbtOps.INSTANCE);


        cacheContainer.setCache(compound, dynamicOps);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return RegisterPackets.PANEL_CACHE_UPDATE;
    }
}
