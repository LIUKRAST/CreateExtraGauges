package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = EGConstants.MOD_ID, dist = Dist.CLIENT)
public class EGClient {

    public EGClient(IEventBus bus, ModContainer container) {
        //Partial models must be initialized on mod loading cause flywheel is mad
        EGPartialModels.init();
        bus.register(this);
        bus.addListener(EGBlockEntityTypes::registerRenderers);
        container.registerConfig(ModConfig.Type.CLIENT, ExtraGaugesConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

}
