package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FactoryPanelBehaviour.class, remap = false)
public interface FactoryPanelBehaviourIMixin {
    @Invoker("notifyRedstoneOutputs")
    void extra_gauges$notifyRedstoneOutputs();
}
