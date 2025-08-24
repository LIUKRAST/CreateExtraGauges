package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.liukrast.eg.api.util.IFPExtra;
import net.minecraft.network.FriendlyByteBuf;

public class FactoryPanelChangeSizePacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    private FactoryPanelBlock.PanelSlot slot;
    private int width;

    public FactoryPanelChangeSizePacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public FactoryPanelChangeSizePacket(FactoryPanelPosition position, int width) {
        super(position.pos());
        this.slot = position.slot();
        this.width = width;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.slot.ordinal());
        buffer.writeInt(width);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.slot = FactoryPanelBlock.PanelSlot.values()[buffer.readVarInt()];
        this.width = buffer.readInt();
    }

    @Override
    protected void applySettings(FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = be.panels.get(slot);
        if (behaviour == null)
            return;
        IFPExtra extra = (IFPExtra) behaviour;
        if(width > 2) extra.extra_gauges$setWidth(width);
        be.notifyUpdate();
    }
}
