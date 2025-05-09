package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumMap;
import java.util.List;

@Mixin(FactoryPanelBlockEntity.class)
public abstract class FactoryPanelBlockEntityMixin extends SmartBlockEntity {

    @Shadow public boolean redraw;

    @Shadow public EnumMap<FactoryPanelBlock.PanelSlot, FactoryPanelBehaviour> panels;

    public FactoryPanelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /* We will avoid adding panels on block entity init, so that custom ones can be loaded from data */
    @ModifyExpressionValue(method = "addBehaviours", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;values()[Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;"))
    private FactoryPanelBlock.PanelSlot[] addBehaviours(FactoryPanelBlock.PanelSlot[] original) {
        return new FactoryPanelBlock.PanelSlot[0];
    }

    @ModifyExpressionValue(method = "addPanel", at = @At(value = "INVOKE", target = "Ljava/util/EnumMap;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T> T addPanel(T original, @Local(argsOnly = true) FactoryPanelBlock.PanelSlot slot) {
        if(original == null) {
            var behaviour = new FactoryPanelBehaviour(FactoryPanelBlockEntity.class.cast(this), slot);
            panels.put(slot, behaviour);
            return (T) behaviour;
        }
        return original;
    }

    @Override
    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
        super.addBehavioursDeferred(behaviours);
        redraw = true;
        for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            if(panels.containsKey(slot)) continue;
            FactoryPanelBehaviour e = new FactoryPanelBehaviour(FactoryPanelBlockEntity.class.cast(this), slot);
            panels.put(slot, e);
            behaviours.add(e);
        }
    }
}
