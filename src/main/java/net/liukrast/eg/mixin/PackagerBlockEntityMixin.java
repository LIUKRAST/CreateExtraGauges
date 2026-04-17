package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mixin(PackagerBlockEntity.class)
public abstract class PackagerBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    public String signBasedAddress;
    @Unique private String extra_gauges$regexData;

    public PackagerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        extra_gauges$regexData = compound.contains("extra_gauges$regexData") ? compound.getString("extra_gauges$regexData") : null;
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if(extra_gauges$regexData != null)
            compound.putString("extra_gauges$regexData", extra_gauges$regexData);
    }

    @Inject(method = "updateSignAddress", at = @At("RETURN"))
    private void updateSignAddress(CallbackInfo ci) {
        for(Direction side : Iterate.directions) {
            var data = extra_gauges$getRegex(side);
            if(data == null) continue;
            extra_gauges$regexData = data.getFirst();
            signBasedAddress = data.getSecond();
            return;
        }
        extra_gauges$regexData = null;
    }

    @Unique
    private Pair<String, String> extra_gauges$getRegex(Direction side) {
        assert level != null;
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
        if(!(blockEntity instanceof FactoryPanelBlockEntity panelContainer))
            return null;
        if(FactoryPanelBlock.connectedDirection(panelContainer.getBlockState()) != side)
            return null;
        for(var panel : panelContainer.panels.values()) {
            if(!(panel instanceof StringPanelBehaviour string)) continue;
            if(!string.hasInteraction("rewriter")) continue;
            return Pair.of(
                    string.getRegex(),
                    string.getReplacement()
            );
        }
        return null;
    }

    @Inject(method = "attemptToSend", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;clearAddress(Lnet/minecraft/world/item/ItemStack;)V"))
    private void attemptToSend(
            List<PackagingRequest> queuedRequests,
            CallbackInfo ci,
            @Share("regex_result") LocalRef<String> regexResult,
            @Local(name = "createdBox") ItemStack createdBox
    ) {
        if(extra_gauges$regexData == null) return;
        try {
            Pattern pattern = Pattern.compile(extra_gauges$regexData);
            regexResult.set(pattern.matcher(PackageItem.getAddress(createdBox)).replaceAll(signBasedAddress));
        } catch (PatternSyntaxException ignored) {}
    }

    @ModifyExpressionValue(method = "attemptToSend", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;signBasedAddress:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
    private String attemptToSend(String original, @Share("regex_result") LocalRef<String> regexResult) {
        var res = regexResult.get();
        if(res == null) return original;
        return regexResult.get();
    }
}
