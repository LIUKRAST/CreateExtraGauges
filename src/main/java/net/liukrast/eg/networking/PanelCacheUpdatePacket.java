package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import net.liukrast.eg.ExtraGaugesConfig;
import net.liukrast.eg.api.util.CacheContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;

public class PanelCacheUpdatePacket extends BlockEntityDataPacket<FactoryPanelBlockEntity> {
    private FactoryPanelBlock.PanelSlot slot;
    private CompoundTag compound;

    public PanelCacheUpdatePacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public PanelCacheUpdatePacket(FactoryPanelPosition position, CompoundTag compound) {
        super(position.pos());
        this.slot = position.slot();
        this.compound = compound;
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.slot.ordinal());
        buffer.writeNbt(compound);
    }

    @Override
    protected void handlePacket(FactoryPanelBlockEntity blockEntity) {
        FactoryPanelBehaviour behaviour = blockEntity.panels.get(slot);
        if(!ExtraGaugesConfig.PANEL_CACHING.get()) return;
        if(!(behaviour instanceof CacheContainer<?> cacheContainer)) return;
        if(Minecraft.getInstance().level == null) return;
        cacheContainer.setCache(compound, NbtOps.INSTANCE);
    }
}
