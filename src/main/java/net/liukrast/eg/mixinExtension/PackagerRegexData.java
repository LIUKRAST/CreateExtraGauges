package net.liukrast.eg.mixinExtension;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface PackagerRegexData {
    @Nullable String extra_gauges$getRegexData();

    @Nullable
    default String extra_gauges$rewriteAddress(ItemStack box, String replacement) {
        String regexData = extra_gauges$getRegexData();
        if(regexData == null) return null;
        try {
            return Pattern.compile(regexData).matcher(PackageItem.getAddress(box)).replaceAll(replacement);
        } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }
}
