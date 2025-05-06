package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionPacket;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FactoryPanelConnectionPacket.class)
public interface FactoryPanelConnectionPacketMixin {
    @Accessor("relocate")
    boolean accessRelocate();

    @Accessor("fromPos")
    FactoryPanelPosition accessFromPos();
}
