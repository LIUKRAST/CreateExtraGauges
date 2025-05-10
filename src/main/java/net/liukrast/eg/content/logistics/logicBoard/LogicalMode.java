package net.liukrast.eg.content.logistics.logicBoard;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.liukrast.eg.content.EGIcons;

import java.util.function.Function;
import java.util.stream.Stream;

public enum LogicalMode implements INamedIconOptions {
    OR(EGIcons.I_OR, stream -> stream.anyMatch(e -> e)),
    AND(EGIcons.I_AND, stream -> stream.allMatch(e -> e)),
    NAND(EGIcons.I_NAND, stream -> !stream.allMatch(e -> e)),
    NOR(EGIcons.I_NOR, stream -> stream.noneMatch(e -> e)),
    XOR(EGIcons.I_XOR, stream -> stream.reduce(false, (a, b) -> a ^ b)),
    XNOR(EGIcons.I_XNOR, stream -> !stream.reduce(false, (a, b) -> a ^ b))
    ;

    private final String translationKey;
    private final AllIcons icon;
    private final Function<Stream<Boolean>, Boolean> function;

    LogicalMode(AllIcons icon, Function<Stream<Boolean>, Boolean> function) {
        this.icon = icon;
        translationKey = "logic_gauge.gate." + Lang.asId(name());
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

    public boolean test(Stream<Boolean> booleanStream) {
        return function.apply(booleanStream);
    }
}
