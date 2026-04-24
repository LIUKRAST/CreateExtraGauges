package net.liukrast.eg.mixin;

import net.createmod.catnip.net.packets.ServerboundConfigPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerboundConfigPacket.class)
public class ServerboundConfigPacketMixin {

    @Inject(method = "serialize", at = @At("HEAD"), cancellable = true)
    private <T> void serialize(T value, CallbackInfoReturnable<String> cir) {
        if(value instanceof String str) {
            cir.setReturnValue(str);
            cir.cancel();
        }
    }

    @Inject(method = "deserialize", at = @At("HEAD"), cancellable = true)
    private static void deserialize(Object type, String sValue, CallbackInfoReturnable<Object> cir) {
        if(type instanceof String) {
            cir.setReturnValue(sValue);
            cir.cancel();
        }
    }
}
