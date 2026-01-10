package net.liukrast.eg;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = EGConstants.MOD_ID, dist = Dist.CLIENT)
public class EGClient {

    public EGClient(IEventBus bus, ModContainer container) {
        EGPartialModels.init();
        bus.register(this);
        bus.addListener(EGBlockEntityTypes::registerRenderers);
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new BaseConfigScreen(parent, modContainer.getModId()));
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

}
