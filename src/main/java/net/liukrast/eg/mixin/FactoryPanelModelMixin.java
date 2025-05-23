package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelModel;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;

@Mixin(FactoryPanelModel.class)
public class FactoryPanelModelMixin {
    @Unique
    private static final ModelProperty<EnumMap<FactoryPanelBlock.PanelSlot, AbstractPanelBehaviour>> PANEL_MODEL = new ModelProperty<>();

    @Inject(method = "gatherModelData", at = @At("RETURN"))
    private void gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData, CallbackInfoReturnable<ModelData.Builder> cir) {
        EnumMap<FactoryPanelBlock.PanelSlot, AbstractPanelBehaviour> states = new EnumMap<>(FactoryPanelBlock.PanelSlot.class);
        for(FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(world, new FactoryPanelPosition(pos, slot));
            if(!(behaviour instanceof AbstractPanelBehaviour abstractBehaviour)) continue;
            states.put(slot, abstractBehaviour);
        }
        cir.getReturnValue().with(PANEL_MODEL, states);
    }

    @ModifyVariable(method = "addPanel", at = @At(value = "STORE", ordinal = 0))
    private PartialModel addPanel(PartialModel model, @Local(argsOnly = true) ModelData modelData, @Local(argsOnly = true) FactoryPanelBlock.PanelSlot slot, @Local(argsOnly = true)FactoryPanelBlock.PanelState panelState, @Local(argsOnly = true)FactoryPanelBlock.PanelType panelType) {
        var panelModel = modelData.get(PANEL_MODEL);
        if(panelModel == null) return model;
        if(!panelModel.containsKey(slot)) return model;
        return panelModel.get(slot).getModel(panelState, panelType);
    }
}
