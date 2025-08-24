package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FactoryPanelBehaviour.class, remap = false)
public interface FactoryPanelBehaviourAccessor {
    @Accessor("timer")
    int getTimer();

    @Accessor("lastReportedLevelInStorage")
    int getLastReportedLevelInStorage();

    @Accessor("lastReportedUnloadedLinks")
    int getLastReportedUnloadedLinks();

    @Accessor("lastReportedPromises")
    int getLastReportedPromises();
}
