package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.eg.content.block.logic.LogicGaugeBehaviour;
import net.liukrast.eg.content.block.logic.LogicGaugeBlock;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import net.liukrast.eg.content.util.FPSBMExtraMethods;
import net.liukrast.eg.registry.RegisterBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FactoryPanelSupportBehaviour.class)
public class FactoryPanelSupportBehaviourMixin implements FPSBMExtraMethods {
    @Shadow private List<FactoryPanelPosition> linkedPanels;

    @Shadow private boolean changed;

    /**
     * The Reason we can suppress this is that argument {@link LogicGaugeBehaviour} should not be added by other mods
     * */
    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void connect(LogicGaugeBehaviour panel) {
        FactoryPanelPosition panelPosition = panel.getPanelPosition();
        if(linkedPanels.contains(panelPosition))
            return;
        linkedPanels.add(panelPosition);
        changed = true;
    }

    /**
     * The Reason we can suppress this is that argument {@link LogicGaugeBehaviour} should not be added by other mods
     * */
    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void disconnect(LogicGaugeBehaviour panel) {
        linkedPanels.remove(panel.getPanelPosition());
        changed = true;
    }

    @Inject(method = "shouldBePoweredTristate", at = @At("HEAD"), cancellable = true)
    private void shouldBePoweredTristate(CallbackInfoReturnable<Boolean> cir) {
        var be = BlockEntityBehaviour.class.cast(this).blockEntity;
        if(!(be instanceof LogicGaugeBlockEntity logicGauge)) return;
        LogicGaugeBehaviour.LogicGate gate = logicGauge.behaviour.get();
        cir.setReturnValue(gate.test(linkedPanels.stream().map(link -> {
            if(!be.getLevel().isLoaded(link.pos())) return gate.nullAction();
            var state = be.getLevel().getBlockState(link.pos());
            if(state.is(RegisterBlocks.LOGIC_GAUGE)) {
                return state.getValue(LogicGaugeBlock.POWERED);
            }
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(be.getLevel(), link);
            if(behaviour == null) return gate.nullAction();
            return behaviour.isActive() && behaviour.satisfied && behaviour.count != 0;
        })));
    }

    @Inject(method = "shouldBePoweredTristate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;at(Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelPosition;)Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;"), cancellable = true)
    private void shouldBePoweredTristate$1(CallbackInfoReturnable<Boolean> cir, @Local FactoryPanelPosition panelPos) {
        var level = BlockEntityBehaviour.class.cast(this).blockEntity.getLevel();
        if(level.getBlockState(panelPos.pos()).is(RegisterBlocks.LOGIC_GAUGE)) {
            cir.setReturnValue(level.getBlockState(panelPos.pos()).getValue(LogicGaugeBlock.POWERED));
            cir.cancel();
        }
    }

    @ModifyExpressionValue(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isLoaded(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean destroy(boolean original, @Local FactoryPanelPosition panelPos) {
        if(!original) return false;
        var be = BlockEntityBehaviour.class.cast(this).blockEntity;
        var level = be.getLevel();
        if(level.getBlockEntity(panelPos.pos()) instanceof LogicGaugeBlockEntity logicGauge) {
            logicGauge.behaviour.targetedByLinks.remove(be.getBlockPos());
            logicGauge.notifyUpdate();
            return false;
        }
        return true;
    }
}
