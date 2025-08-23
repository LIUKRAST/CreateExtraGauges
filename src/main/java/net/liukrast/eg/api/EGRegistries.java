package net.liukrast.eg.api;

import com.simibubi.create.Create;
import net.liukrast.eg.EGConstants;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EGRegistries {
    public static final ResourceKey<Registry<PanelType<?>>> PANEL_REGISTRY_KEY = ResourceKey.createRegistryKey(EGConstants.id("panels"));
    public static final Registry<PanelType<?>> PANEL_REGISTRY = new RegistryBuilder<>(PANEL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(Create.asResource("factory"))
            .create();

    public static final ResourceKey<Registry<PanelConnection<?>>> PANEL_CONNECTION_REGISTRY_KEY = ResourceKey.createRegistryKey(EGConstants.id("panel_connections"));
    public static final Registry<PanelConnection<?>> PANEL_CONNECTION_REGISTRY = new RegistryBuilder<>(PANEL_CONNECTION_REGISTRY_KEY)
            .sync(true)
            .defaultKey(EGConstants.id("redstone"))
            .create();
}
