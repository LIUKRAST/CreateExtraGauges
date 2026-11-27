package net.liukrast.eg.mixinExtension;

import org.jetbrains.annotations.Nullable;

public final class UnwrappedPackageAddress {
    private static final ThreadLocal<String> ADDRESS = new ThreadLocal<>();

    private UnwrappedPackageAddress() {}

    public static void clear() {
        ADDRESS.remove();
    }

    public static void record(String address) {
        if(ADDRESS.get() == null) ADDRESS.set(address);
    }

    @Nullable
    public static String take() {
        String address = ADDRESS.get();
        ADDRESS.remove();
        return address;
    }
}
