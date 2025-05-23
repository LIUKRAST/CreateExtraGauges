package net.liukrast.eg.api;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.registry.PanelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class GaugeRegistry {
    public static final ResourceKey<Registry<PanelType<?>>> PANEL_REGISTRY_KEY = ResourceKey.createRegistryKey(ExtraGauges.id("panels"));

    public static Supplier<IForgeRegistry<PanelType<?>>> PANEL_REGISTRY = () -> null;

}
