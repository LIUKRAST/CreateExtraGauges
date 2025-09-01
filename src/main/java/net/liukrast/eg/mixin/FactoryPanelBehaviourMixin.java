package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.liukrast.eg.mixinExtension.WidthModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FactoryPanelBehaviour.class)
public class FactoryPanelBehaviourMixin implements WidthModifier {
    /* UNIQUE VALUES */
    @Unique private int extra_gauges$width = 3;
    /* IMPL METHODS */
    @Override public int extra_gauges$getWidth() {return extra_gauges$width;}
    @Override public void extra_gauges$setWidth(int width) {extra_gauges$width = width;}

    /* DATA */
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putUUID(Ljava/lang/String;Ljava/util/UUID;)V"))
    private void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        if(extra_gauges$width != 3) panelTag.putInt("extra_gauges$CraftWidth", extra_gauges$width);
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci, @Local(ordinal = 1) CompoundTag panelTag) {
        extra_gauges$width = panelTag.contains("extra_gauges$CraftWidth") ? panelTag.getInt("extra_gauges$CraftWidth") : 3;
    }
}
