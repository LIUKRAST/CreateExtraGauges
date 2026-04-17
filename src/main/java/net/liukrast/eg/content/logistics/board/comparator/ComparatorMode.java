package net.liukrast.eg.content.logistics.board.comparator;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.liukrast.eg.content.EGIcons;

public enum ComparatorMode implements INamedIconOptions {
    STATIC(EGIcons.I_STATIC),
    ADVANCED(EGIcons.I_ADVANCED);

    private final String translationKey;
    private final AllIcons icon;

    ComparatorMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "comparator_gauge.mode." + Lang.asId(name());
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }
}
