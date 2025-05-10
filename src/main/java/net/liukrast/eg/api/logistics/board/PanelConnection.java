package net.liukrast.eg.api.logistics.board;

import java.util.function.Supplier;

/**
 *
 * */
public abstract class PanelConnection<T> {
    public abstract int getColor(Supplier<T> informationA, Supplier<T> informationB);

    /**
     * UNSAFE
     * */
    @SuppressWarnings("unchecked")
    public int getColorGeneric(Supplier<?> supplier, Supplier<?> supplier1) {
        return getColor((Supplier<T>) supplier, (Supplier<T>) supplier1);
    }
}
