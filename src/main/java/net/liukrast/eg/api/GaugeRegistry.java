package net.liukrast.eg.api;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class GaugeRegistry {
    public static final ResourceKey<Registry<PanelType<?>>> PANEL_REGISTRY_KEY = ResourceKey.createRegistryKey(ExtraGauges.id("panels"));
    public static final Registry<PanelType<?>> PANEL_REGISTRY = new RegistryBuilder<>(PANEL_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ExtraGauges.id("empty"))
            .create();
}
