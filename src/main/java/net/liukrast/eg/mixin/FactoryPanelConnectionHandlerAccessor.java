package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FactoryPanelConnectionHandler.class)
public interface FactoryPanelConnectionHandlerAccessor {

    @Accessor("relocating")
    public static void setRelocating(boolean relocating) {
        throw new AssertionError();
    }

    @Accessor("connectingFrom")
    public static void setConnectingFrom(FactoryPanelPosition connectingFrom) {
        throw new AssertionError();
    }

    @Accessor("connectingFromBox")
    public static void setConnectingFromBox(AABB connectingFromBox) {
        throw new AssertionError();
    }
}
