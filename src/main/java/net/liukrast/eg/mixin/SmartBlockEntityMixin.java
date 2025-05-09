package net.liukrast.eg.mixin;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.api.GaugeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmartBlockEntity.class)
public class SmartBlockEntityMixin {

    /*@Inject(method = "read", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;addBehavioursDeferred(Ljava/util/List;)V"))
    private void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if(!(SmartBlockEntity.class.cast(this) instanceof FactoryPanelBlockEntity blockEntity)) return;
        if(!tag.contains("CustomPanels")) return;
        var special = tag.getCompound("CustomPanels");
        for(FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            if(!special.contains(CreateLang.asId(slot.name()))) continue;
            String id = special.getString(CreateLang.asId(slot.name()));
            var customPanel = GaugeRegistry.PANEL_REGISTRY.get(ResourceLocation.parse(id));
            if(customPanel == null) throw new IllegalStateException("Unable to find panel for id " + id);
            var init = customPanel.create(blockEntity, slot);
            init.active = true;
            blockEntity.attachBehaviourLate(init);
            blockEntity.panels.put(slot, init);
            blockEntity.redraw = true;
            blockEntity.lastShape = null;
            blockEntity.notifyUpdate();
        }
    }*/


}
