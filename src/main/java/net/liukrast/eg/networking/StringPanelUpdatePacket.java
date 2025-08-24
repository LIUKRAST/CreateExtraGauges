package net.liukrast.eg.networking;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.minecraft.network.FriendlyByteBuf;

public class StringPanelUpdatePacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    private FactoryPanelBlock.PanelSlot slot;
    private String join,regex,replace;

    public StringPanelUpdatePacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public StringPanelUpdatePacket(FactoryPanelPosition position, String join, String regex, String replace) {
        super(position.pos());
        this.slot = position.slot();
        this.join = join;
        this.regex = regex;
        this.replace = replace;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buf) {
        buf.writeVarInt(this.slot.ordinal());
        buf.writeUtf(join);
        buf.writeUtf(regex);
        buf.writeUtf(replace);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buf) {
        this.slot = FactoryPanelBlock.PanelSlot.values()[buf.readVarInt()];
        this.join = buf.readUtf();
        this.regex = buf.readUtf();
        this.replace = buf.readUtf();
    }

    @Override
    protected void applySettings(FactoryPanelBlockEntity factoryPanelBlockEntity) {
        FactoryPanelBehaviour behaviour = factoryPanelBlockEntity.panels.get(slot);
        if(!(behaviour instanceof StringPanelBehaviour stringPanel)) return;
        stringPanel.setFilter(join, regex, replace);
    }
}
