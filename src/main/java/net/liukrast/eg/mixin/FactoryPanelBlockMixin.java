package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.liukrast.eg.api.logistics.board.PanelBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(value = FactoryPanelBlock.class)
public abstract class FactoryPanelBlockMixin extends Block {

    public FactoryPanelBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if(level.getBlockEntity(pos) instanceof FactoryPanelBlockEntity be) {
            var location = target.getLocation();
            FactoryPanelBlock.PanelSlot slot = getTargetedSlot(pos, state, location);
            if(be.panels.get(slot) instanceof AbstractPanelBehaviour ab && ab.active) {
                return ab.getItem().getDefaultInstance();
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        return new ArrayList<>();
    }

    @Shadow(remap = false)
    public static FactoryPanelBlock.PanelSlot getTargetedSlot(BlockPos pos, BlockState blockState, Vec3 clickLocation) {
        throw new AssertionError("Mixin injection failed");
    }

    @ModifyExpressionValue(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean getStateForPlacement(boolean original, @Local(argsOnly = true) BlockPlaceContext context, @Local FactoryPanelBlockEntity blockEntity, @Local(ordinal = 1) BlockState state, @Local Vec3 location) {
        if(original) return true;
        if(!(context.getItemInHand().getItem() instanceof PanelBlockItem panelBlockItem)) return false;
        panelBlockItem.applyExtraPlacementData(context, blockEntity, getTargetedSlot(context.getClickedPos(), state, location));
        return true;
    }

    @ModifyArg(method = "lambda$onSneakWrenched$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;)V", remap = true), remap = false)
    private static ItemStack lambda$onSneakWrenched$0(ItemStack original, @Local FactoryPanelBehaviour behaviour) {
        if(!(behaviour instanceof AbstractPanelBehaviour abstractPanelBehaviour)) return original;
        return abstractPanelBehaviour.getItem().getDefaultInstance();
    }

    @WrapWithCondition(method = "setPlacedBy", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock;withBlockEntityDo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Ljava/util/function/Consumer;)V", remap = false))
    private boolean setPlacedBy(FactoryPanelBlock instance, BlockGetter blockGetter, BlockPos pos, Consumer<FactoryPanelBlockEntity> consumer, @Local(argsOnly = true) ItemStack stack, @Local FactoryPanelBlock.PanelSlot initialSlot) {
        if(!(stack.getItem() instanceof PanelBlockItem panelBlockItem)) return true;
        FactoryPanelBlock.class.cast(this).withBlockEntityDo(blockGetter, pos, blockEntity -> panelBlockItem.applyToSlot(blockEntity, initialSlot, LogisticallyLinkedBlockItem.networkFromStack(FactoryPanelBlockItem.fixCtrlCopiedStack(stack))));
        return false;
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", remap = false), cancellable = true)
    private void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, @Local ItemStack stack) {
        if(!(stack.getItem() instanceof PanelBlockItem panelBlockItem)) return;
        if(level.getBlockEntity(pos) instanceof FactoryPanelBlockEntity panel && panel.restocker) {
            AllSoundEvents.DENY.playOnServer(level, pos);
            player.displayClientMessage(Component.translatable("extra_gauges.panel.custom_panel_on_restocker"), true);
            cir.setReturnValue(InteractionResult.CONSUME);
            cir.cancel();
            return;
        }
        var error = panelBlockItem.isReadyForPlacement(stack, level, pos, player);
        if(error != null) {
            AllSoundEvents.DENY.playOnServer(level, pos);
            player.displayClientMessage(error, true);
            cir.setReturnValue(InteractionResult.CONSUME);
            cir.cancel();
        }
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", remap = false))
    private boolean use(boolean original, @Local ItemStack stack) {
        return original || stack.getItem() instanceof PanelBlockItem;
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockItem;isTuned(Lnet/minecraft/world/item/ItemStack;)Z", remap = false))
    private boolean use$1(boolean original, @Local ItemStack stack) {
        return original || stack.getItem() instanceof PanelBlockItem;
    }

    @WrapOperation(method = "lambda$use$2", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;addPanel(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;Ljava/util/UUID;)Z"), remap = false)
    private boolean lambda$use$2(FactoryPanelBlockEntity instance, FactoryPanelBlock.PanelSlot panelSlot, UUID slot, Operation<Boolean> original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) FactoryPanelBlockEntity blockEntity, @Local(argsOnly = true) FactoryPanelBlock.PanelSlot newSlot) {
        if(stack.getItem() instanceof PanelBlockItem blockItem) return blockItem.applyToSlot(blockEntity, newSlot, LogisticallyLinkedBlockItem.networkFromStack(FactoryPanelBlockItem.fixCtrlCopiedStack(stack)));
        return original.call(instance, panelSlot, slot);
    }

    @ModifyArg(method = "lambda$use$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V", remap = true), remap = false)
    private Component lambda$use$2(Component message, @Local(argsOnly = true) ItemStack stack) {
        if(!(stack.getItem() instanceof PanelBlockItem blockItem)) return message;
        return blockItem.getPlacedMessage();
    }

    @Unique
    private static ItemStack extra_gauges$stored$itemStack;

    @Inject(method = "lambda$tryDestroySubPanelFirst$3", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;activePanels()I"), remap = false)
    private static void lambda$tryDestroySubPanelFirst$3(FactoryPanelBlock.PanelSlot destroyedSlot, Player player, Level level, BlockPos pos, FactoryPanelBlockEntity fpbe, CallbackInfoReturnable<InteractionResult> cir) {
        var behaviour = fpbe.panels.get(destroyedSlot);
        if(!(behaviour instanceof AbstractPanelBehaviour panelBehaviour)) extra_gauges$stored$itemStack = null;
        else extra_gauges$stored$itemStack = panelBehaviour.getItem().getDefaultInstance();
    }

    @ModifyArg(method = "lambda$tryDestroySubPanelFirst$3", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", remap = true), remap = false)
    private static ItemStack lambda$tryDestroySubPanelFirst$3(ItemStack stack) {
        return extra_gauges$stored$itemStack == null ? stack : extra_gauges$stored$itemStack;
    }

    @ModifyExpressionValue(method = "canBeReplaced", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z", remap = false))
    private boolean canBeReplaced(boolean original, @Local(argsOnly = true) BlockPlaceContext context) {
        return original || context.getItemInHand().getItem() instanceof PanelBlockItem;
    }

    @WrapOperation(method = "canBeReplaced", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;isActive()Z", remap = false))
    private boolean canBeReplaced(FactoryPanelBehaviour instance, Operation<Boolean> original) {
        if(instance == null) return false;
        return original.call(instance);
    }
}
