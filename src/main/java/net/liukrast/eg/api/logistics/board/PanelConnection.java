package net.liukrast.eg.api.logistics.board;

import java.util.function.Supplier;

/**
 *
 * */
public abstract class PanelConnection<T> {
    public abstract int getColor(T from, T to);

    /**
     * UNSAFE
     * */
    @SuppressWarnings("unchecked")
    public int getColorGeneric(Supplier<?> from, Supplier<?> to) {
        return getColor((T) from.get(), (T) to.get());
    }
}
