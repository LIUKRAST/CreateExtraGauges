package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.content.logistics.logicBoard.LogicPanelBehaviour;
import net.liukrast.eg.registry.RegisterPanels;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

import java.util.EnumMap;
import java.util.Objects;

@Mixin(FactoryPanelBlockEntity.class)
public abstract class FactoryPanelBlockEntityMixin {

    @Shadow public EnumMap<PanelSlot, FactoryPanelBehaviour> panels;

    /*
    @ModifyExpressionValue(method = "addBehaviours", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;values()[Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;"))
    private PanelSlot[] addBehaviours(PanelSlot[] original) {
        return new PanelSlot[0];
    }

    @ModifyVariable(method = "addPanel", at = @At("STORE"))
    private FactoryPanelBehaviour addPanel(FactoryPanelBehaviour original, @Local(argsOnly = true) PanelSlot slot) {
        if(original == null) {
            var behaviour = new FactoryPanelBehaviour(FactoryPanelBlockEntity.class.cast(this), slot);
            panels.put(slot, behaviour);
            return behaviour;
        }
        return original;
    }*/

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
