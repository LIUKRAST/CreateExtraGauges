package net.liukrast.eg.mixin;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(FilteringBehaviour.class)
public interface FilteringBehaviourMixin {
    @Accessor("slotPositioning")
    void setValueBoxTransform(ValueBoxTransform value);
}
