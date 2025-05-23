package net.liukrast.eg;

import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.eg.api.GaugeRegistry;
import net.liukrast.eg.api.event.AbstractPanelRenderEvent;
import net.liukrast.eg.datagen.ExtraGaugesItemModelProvider;
import net.liukrast.eg.registry.RegisterCreativeModeTabs;
import net.liukrast.eg.registry.RegisterItems;
import net.liukrast.eg.registry.RegisterPanels;
import net.liukrast.eg.registry.RegisterPartialModels;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(ExtraGauges.MOD_ID)
public class ExtraGauges {
    public static final String MOD_ID = "extra_gauges";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public ExtraGauges() {
        @SuppressWarnings("removal") var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegisterItems.register(modEventBus);
        RegisterPanels.register(modEventBus);
        RegisterCreativeModeTabs.register(modEventBus);
        modEventBus.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::abstractPanelRender);
    }

    @SubscribeEvent
    public void fMLClientSetup(FMLClientSetupEvent event) {
        RegisterPartialModels.init();
        PonderIndex.addPlugin(new ExtraGaugesPonderPlugin());
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new ExtraGaugesItemModelProvider(packOutput, helper));
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        GaugeRegistry.PANEL_REGISTRY = event.create(RegistryBuilder.of(ExtraGauges.id("panels"))); //TODO: Is this correct?
    }

    //Using Forge event bus
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
