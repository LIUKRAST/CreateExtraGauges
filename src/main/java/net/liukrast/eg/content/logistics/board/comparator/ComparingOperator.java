package net.liukrast.eg.content.logistics.board.comparator;

import net.createmod.catnip.lang.Lang;

public enum ComparingOperator {
    EQUALS(0, (a, b) -> a == b, "="),
    DIFFERENT(16, (a, b) -> a != b, "!="),
    GREATER(32, (a, b) -> a > b, ">"),
    GREATER_EQUALS(48, (a, b) -> a >= b, ">="),
    LESS(64, (a, b) -> a < b, "<"),
    LESS_EQUALS(80, (a, b) -> a <= b, "<=");

    private final String translationKey;
    private final Float2BooleanBiFunction function;
    ComparingOperator(int index, Float2BooleanBiFunction biFunction, String character) {
        translationKey = "comparator_gauge.mode." + Lang.asId(name());
        this.function = biFunction;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean test(float a, float b) {
        return function.apply(a, b);
    }

    private interface Float2BooleanBiFunction {
        boolean apply(float a, float b);
    }
}
