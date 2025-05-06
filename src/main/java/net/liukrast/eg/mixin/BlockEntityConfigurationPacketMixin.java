package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionPacket;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.liukrast.eg.content.block.logic.LogicGaugeBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityConfigurationPacket.class)
public class BlockEntityConfigurationPacketMixin {

    @WrapOperation(method = "handle", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/networking/BlockEntityConfigurationPacket;applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/foundation/blockEntity/SyncedBlockEntity;)V"))
    private void handle(BlockEntityConfigurationPacket<SyncedBlockEntity> instance, ServerPlayer player, SyncedBlockEntity be, Operation<Void> original) {
        var that = BlockEntityConfigurationPacket.class.cast(this);
        if(!(that instanceof FactoryPanelConnectionPacket packet)) {
            original.call(instance, player, be);
            return;
        }
        if(be instanceof LogicGaugeBlockEntity logicGauge) {
            var mixinPacket = ((FactoryPanelConnectionPacketMixin)packet);
            if(mixinPacket.accessRelocate()) {
                //TODO: Relocate logic
            } else logicGauge.behaviour.addConnection(mixinPacket.accessFromPos());
            return;
        }
        original.call(instance, player, be);
    }
}
