package net.liukrast.eg.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.FactoryGaugeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import net.createmod.catnip.data.IntAttached;
import net.liukrast.eg.api.logistics.board.AbstractPanelBehaviour;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(value = FactoryGaugeDisplaySource.class, remap = false)
public abstract class FactoryGaugeDisplaySourceMixin extends ValueListDisplaySource {
    /*
     * abstract panel behaviors will ignore the default display link
     * */
    @Inject(
            method = "createEntry",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;getFilter()Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true
    )
    private void createEntry(Level level, FactoryPanelPosition pos, CallbackInfoReturnable<IntAttached<MutableComponent>> cir, @Local FactoryPanelBehaviour panel) {
        if(panel instanceof AbstractPanelBehaviour) cir.setReturnValue(null);
    }

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        var list = super.provideText(context, stats);
        var list1 = context.blockEntity().factoryPanelSupport.getLinkedPanels().stream().map(pos -> {
            var panel = FactoryPanelBehaviour.at(context.level(), pos);
            if(panel instanceof AbstractPanelBehaviour ab) return ab.getDisplayLinkComponent(shortenNumbers(context));
            return null;
        }).filter(Objects::nonNull).limit(stats.maxRows());
        return Stream.concat(list.stream(), list1).toList();
    }

    @Override
    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
        var list = super.provideFlapDisplayText(context, stats);
        var list1 = context.blockEntity().factoryPanelSupport.getLinkedPanels().stream().map(pos -> {
            var panel = FactoryPanelBehaviour.at(context.level(), pos);
            if(panel instanceof AbstractPanelBehaviour ab) return ab.getDisplayLinkComponent(shortenNumbers(context));
            return null;
        }).filter(Objects::nonNull).limit(stats.maxRows()).map(Arrays::asList);
        return Stream.concat(list.stream(), list1).toList();
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        if (!layout.isLayout("Default"))
            layout.loadDefault(flapDisplay.getMaxCharCount());
    }
}
