package net.liukrast.eg.api;

import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class EGRegistries {
    public static final ResourceKey<Registry<PanelType<?>>> PANEL_REGISTRY_KEY = ResourceKey.createRegistryKey(EGConstants.id("panels"));
    public static final ResourceKey<Registry<PanelConnection<?>>> PANEL_CONNECTION_REGISTRY_KEY = ResourceKey.createRegistryKey(EGConstants.id("panel_connections"));

    public static Supplier<IForgeRegistry<PanelType<?>>> PANEL_REGISTRY = () -> null;
    public static Supplier<IForgeRegistry<PanelConnection<?>>> PANEL_CONNECTION_REGISTRY = () -> null;
}
