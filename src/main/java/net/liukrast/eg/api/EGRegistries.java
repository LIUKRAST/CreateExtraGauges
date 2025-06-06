package net.liukrast.eg.api;

import com.simibubi.create.Create;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.logistics.board.PanelConnection;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Set;

public class EGRegistries {
    public static final ResourceKey<Registry<PanelType<?>>> PANEL_REGISTRY_KEY = ResourceKey.createRegistryKey(ExtraGauges.id("panels"));
    public static final Registry<PanelType<?>> PANEL_REGISTRY = new RegistryBuilder<>(PANEL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(Create.asResource("factory"))
            .create();

    public static final ResourceKey<Registry<PanelConnection<?>>> PANEL_CONNECTION_REGISTRY_KEY = ResourceKey.createRegistryKey(ExtraGauges.id("panel_connections"));
    public static final Registry<PanelConnection<?>> PANEL_CONNECTION_REGISTRY = new RegistryBuilder<>(PANEL_CONNECTION_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ExtraGauges.id("redstone"))
            .create();
}
