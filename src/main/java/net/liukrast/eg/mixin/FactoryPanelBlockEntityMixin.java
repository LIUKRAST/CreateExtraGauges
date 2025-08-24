package net.liukrast.eg.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Objects;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

@Mixin(value = FactoryPanelBlockEntity.class, remap = false)
public abstract class FactoryPanelBlockEntityMixin {

    @Shadow public EnumMap<PanelSlot, FactoryPanelBehaviour> panels;

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        var instance = FactoryPanelBlockEntity.class.cast(this);
        for(PanelSlot slot : PanelSlot.values()) {
            String key = CreateLang.asId(slot.name());
            FactoryPanelBehaviour behaviour = null;
            if(tag.contains("CustomPanels")) {
                var customPanels = tag.getCompound("CustomPanels");
                if(customPanels.contains(key)) {
                    ResourceLocation id = ResourceLocation.parse(customPanels.getString(key));
                    var type = Objects.requireNonNull(EGRegistries.PANEL_REGISTRY.get().getValue(id));
                    var current = panels.get(slot);
                    if(current != null && type.asClass().equals(current.getClass())) continue; //No need to re-create the behavior
                    behaviour = type.create(instance, slot);
                }
            }
            if(behaviour != null) {
                panels.put(slot, behaviour);
                instance.attachBehaviourLate(behaviour);
            }
        }
    }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void destroy(CallbackInfo ci) {
        var instance = FactoryPanelBlockEntity.class.cast(this);
        for(var panel : panels.values()) {
            if(!panel.active) continue;
            Block.popResource(Objects.requireNonNull(instance.getLevel()), instance.getBlockPos(), panel instanceof AbstractPanelBehaviour ab ? ab.getItem().getDefaultInstance() : AllBlocks.FACTORY_GAUGE.asStack());
        }
    }
}
