package net.liukrast.eg;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ItemLike;

public class EGLang extends Lang {
    public static String getTooltip(ItemLike itemLike) {
        return  itemLike.asItem().getDescriptionId() + ".tooltip.summary";
    }

    public static MutableComponent translateDirect(String key, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(EGConstants.MOD_ID + "." + key, args1);
    }

    //

    public static LangBuilder builder() {
        return new LangBuilder(EGConstants.MOD_ID);
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }
}
