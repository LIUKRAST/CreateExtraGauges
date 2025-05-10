package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.GaugeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

import java.util.EnumMap;
import java.util.Objects;

@Mixin(FactoryPanelBlockEntity.class)
public abstract class FactoryPanelBlockEntityMixin {

    @Shadow public EnumMap<PanelSlot, FactoryPanelBehaviour> panels;

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        var instance = FactoryPanelBlockEntity.class.cast(this);
        for(PanelSlot slot : PanelSlot.values()) {
            String key = CreateLang.asId(slot.name());
            FactoryPanelBehaviour behaviour = null;
            if(tag.contains("CustomPanels")) {
                var customPanels = tag.getCompound("CustomPanels");
                if(customPanels.contains(key)) {
                    ResourceLocation id = ResourceLocation.parse(customPanels.getString(key));
                    var type = Objects.requireNonNull(GaugeRegistry.PANEL_REGISTRY.get(id));
                    if(type.asClass().equals(panels.get(slot).getClass())) continue; //No need to re-create the behavior
                    behaviour = type.create(instance, slot);
                }
            }
            if(behaviour == null) behaviour = new FactoryPanelBehaviour(instance, slot);
            this.panels.put(slot, behaviour);
            instance.attachBehaviourLate(behaviour);
        }
    }
}
