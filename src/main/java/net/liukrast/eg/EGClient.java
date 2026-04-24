package net.liukrast.eg;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.deployer.lib.DeployerClient;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.deployer.lib.logistics.board.screen.OutputSlot;
import net.liukrast.eg.content.logistics.board.PassivePanelBehaviour;
import net.liukrast.eg.content.logistics.board.ShowSourceIdOverlay;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.liukrast.eg.registry.EGPanels;
import net.liukrast.eg.registry.EGPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "extra_gauges", dist = Dist.CLIENT)
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
        ClientRegisterHelpers.registerGaugeSlot(EGPanels.PASSIVE.get(), (a,b) -> new OutputSlot.Item<>(a,b, PassivePanelBehaviour::getFilter));
        ClientRegisterHelpers.registerSpecialHovering(() -> DeployerClient.SELECTED_CONNECTION != null, new ShowSourceIdOverlay());
    }

}
