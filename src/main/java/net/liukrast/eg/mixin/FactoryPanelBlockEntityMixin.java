package net.liukrast.eg.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Objects;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

@Mixin(value = FactoryPanelBlockEntity.class)
public abstract class FactoryPanelBlockEntityMixin extends SmartBlockEntity {

    @Shadow(remap = false) public EnumMap<PanelSlot, FactoryPanelBehaviour> panels;

    public FactoryPanelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "read", at = @At("HEAD"), remap = false)
    private void read(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        var instance = FactoryPanelBlockEntity.class.cast(this);
        if(!tag.contains("CustomPanels")) return;
        var customPanels = tag.getCompound("CustomPanels");
        for(PanelSlot slot : PanelSlot.values()) {
            String key = CreateLang.asId(slot.name());
            if(customPanels.contains(key)) {
                ResourceLocation id = ResourceLocation.parse(customPanels.getString(key));
                var type = EGRegistries.PANEL_REGISTRY.get().getValue(id);

                if (type == null) {
                    EGConstants.LOGGER.error("Unable to find panel registry key {} for panel slot {} at pos {}", id, slot, getBlockPos());
                    continue;
                }

                var current = panels.get(slot);
                if(current != null && type.asClass().equals(current.getClass())) continue; //No need to re-initialize behaviour

                var behaviour = type.create(instance, slot);
                if(behaviour == null) continue;

                panels.put(slot, behaviour);
                instance.attachBehaviourLate(behaviour);
            }
        }
    }

    @Inject(method = "destroy", at = @At("HEAD"), remap = false)
    private void destroy(CallbackInfo ci) {
        var instance = FactoryPanelBlockEntity.class.cast(this);
        for(var panel : panels.values()) {
            if(!panel.active) continue;
            Block.popResource(Objects.requireNonNull(instance.getLevel()), instance.getBlockPos(), panel instanceof AbstractPanelBehaviour ab ? ab.getItem().getDefaultInstance() : AllBlocks.FACTORY_GAUGE.asStack());
        }
    }
}
