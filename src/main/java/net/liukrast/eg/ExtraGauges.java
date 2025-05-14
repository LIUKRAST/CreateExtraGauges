package net.liukrast.eg;

import com.simibubi.create.AllCreativeModeTabs;
import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.event.AbstractPanelRenderEvent;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPanels;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges(IEventBus modEventBus) {
        RegisterItems.register(modEventBus);
        RegisterPanels.register(modEventBus);
        modEventBus.register(this);
        NeoForge.EVENT_BUS.addListener(this::abstractPanelRender);
    }

    @SubscribeEvent
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()) {
            event.accept(RegisterItems.LOGIC_GAUGE);
            event.accept(RegisterItems.INT_GAUGE);
            event.accept(RegisterItems.COMPARATOR_GAUGE);
        }
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        RegisterPartialModels.init();
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

    @SubscribeEvent
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
    }

    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        event.register(GaugeRegistry.PANEL_REGISTRY);
    }

    //Using NeoForge event bus
    public void abstractPanelRender(AbstractPanelRenderEvent event) {
        var ms = event.poseStack;
        var buffer = event.bufferSource;
        /*if(event.behaviour instanceof IntPanelBehaviour behaviour) {
            var blockState = behaviour.blockEntity.getBlockState();
            float xRot = FactoryPanelBlock.getXRot(blockState) + Mth.PI / 2;
            float yRot = FactoryPanelBlock.getYRot(blockState);

            var msr = TransformStack.of(ms);
            msr
                    .scale(0.025f, 0.025f, 0.025f)
                    .rotateCentered(yRot + Mth.PI/2, Direction.UP)
                    .rotateCentered(xRot +Mth.PI/2, Direction.EAST)
                    .rotateCentered(Mth.PI/2, Direction.UP)
                    .translate(behaviour.slot.xOffset*.5, 0, behaviour.slot.yOffset * .5);

            NixieTubeRenderer.drawInWorldString(ms, buffer, String.valueOf(behaviour.count), 16777215);
        }
        /*if(event.behaviour instanceof ScrollPanelBehaviour<?> scrollPanelBehaviour) { TODO
            scrollPanelBehaviour.get().getIcon().render(event.poseStack, event.bufferSource, 16777216);
        }*/
    }
}
