package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.liukrast.eg.content.EGIcons;

public enum ComparatorMode implements INamedIconOptions {
    EQUALS(EGIcons.I_EQUALS, (a, b) -> a == b, "="),
    DIFFERENT(EGIcons.I_DIFFERENT, (a, b) -> a != b, "!="),
    GREATER(EGIcons.I_GREATER, (a, b) -> a > b, ">"),
    GREATER_EQUALS(EGIcons.I_GREATER_EQUALS, (a, b) -> a >= b, ">="),
    LESS(EGIcons.I_LESS, (a, b) -> a < b, "<"),
    LESS_EQUALS(EGIcons.I_LESS_EQUALS, (a, b) -> a <= b, "<=");

    private final String translationKey;
    private final AllIcons icon;
    private final Int2BooleanBiFunction function;
    private final String character;

    ComparatorMode(AllIcons icon, Int2BooleanBiFunction biFunction, String character) {
        this.icon = icon;
        translationKey = "comparator_gauge.mode." + Lang.asId(name());
        this.function = biFunction;
        this.character = character;
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    public String character() {
        return character;
    }

    public boolean test(int a, int b) {
        return function.apply(a, b);
    }

    private interface Int2BooleanBiFunction {
        boolean apply(int a, int b);
    }
}
