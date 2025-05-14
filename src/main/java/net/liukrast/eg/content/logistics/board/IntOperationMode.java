package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.liukrast.eg.content.EGIcons;

import java.util.function.Function;
import java.util.stream.Stream;

public enum IntOperationMode implements INamedIconOptions {
    ADD(AllIcons.I_ADD, stream -> stream.mapToInt(Integer::intValue).sum()),
    SUBTRACT(EGIcons.I_SUBTRACT, stream -> -stream.mapToInt(Integer::intValue).sum()),
    MULTIPLY(EGIcons.I_MULTIPLY, stream -> stream.mapToInt(Integer::intValue).reduce(1, (left, right) -> left*right));

    private final String translationKey;
    private final AllIcons icon;
    private final Function<Stream<Integer>, Integer> function;

    IntOperationMode(AllIcons icon, Function<Stream<Integer>, Integer> function) {
        this.icon = icon;
        translationKey = "integer_gauge.mode." + Lang.asId(name());
        this.function = function;
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    public int test(Stream<Integer> booleanStream) {
        return function.apply(booleanStream);
    }
}
