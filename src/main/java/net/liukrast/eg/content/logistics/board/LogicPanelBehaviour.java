package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.ScrollOptionPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public class LogicPanelBehaviour extends ScrollOptionPanelBehaviour<LogicalMode> {

    public LogicPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.logic_gate"), type, be, slot, LogicalMode.class);
    }

    /* IMPL */
    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.REDSTONE, () -> !redstonePowered);
        builder.registerOutput(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
    }

    @Override
    public Item getItem() {
        return EGItems.LOGIC_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.LOGIC_PANEL;
    }

    /* DATA */
    /* UPDATE */

    @Override
    public void notifiedFromInput() {
        if(!active)
            return;
        var result = getAllValues(DeployerPanelConnections.REDSTONE.get());
        if(result == null) return;
        boolean shouldPower = get().test(result.stream());
        if(shouldPower != redstonePowered)
            return;
        redstonePowered = !shouldPower;
        blockEntity.notifyUpdate();
        for(FactoryPanelPosition panelPos : targeting) {
            if(!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if(behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        notifyOutputs();
    }

    @Override
    public BulbState getBulbState() {
        return redstonePowered ? BulbState.RED : BulbState.DISABLED;
    }

    /* DISPLAY LINK */
    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        boolean active = getConnectionValue(DeployerPanelConnections.REDSTONE).orElse(false);
        String t = "✔";
        String f = "✖";
        if(!shortened) {
            t += " True";
            f += " False";
        }
        return Component.literal(active ? t : f);
    }
}
