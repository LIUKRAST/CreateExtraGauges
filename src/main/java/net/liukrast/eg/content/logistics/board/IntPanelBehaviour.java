package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.ScrollOptionPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.List;

public class IntPanelBehaviour extends ScrollOptionPanelBehaviour<IntOperationMode> {
    public IntPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.int_operation"), type, be, slot, IntOperationMode.class);
    }

    /* IMPL */

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.NUMBERS, () -> (float)count);
        builder.registerInput(DeployerPanelConnections.REDSTONE);
        builder.registerOutput(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
    }

    @Override
    public Item getItem() {
        return EGItems.INT_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.INT_PANEL;
    }

    /* DATA */
    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("Count", count);
        nbt.putBoolean("RedstonePowered", redstonePowered);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        count = nbt.getInt("Count");
        redstonePowered = nbt.getBoolean("RedstonePowered");
    }

    /* UPDATE */

    @Override
    public void notifiedFromInput() {
        if(!active)
            return;
        List<Boolean> flagList = getAllValues(DeployerPanelConnections.REDSTONE.get());
        if(flagList == null) return;

        boolean flagged = flagList.stream().anyMatch(bl -> bl);

        int result;
        if(!flagged) {
            List<Float> countList = getAllValues(DeployerPanelConnections.NUMBERS.get());
            if(countList == null) return;
            result = get().process(countList.stream().map(f -> (int)(float)f));
        } else if(get() == IntOperationMode.MEMORY) {
            if(redstonePowered != flagged) {
                this.redstonePowered = flagged;
                blockEntity.notifyUpdate();
            }
            return;
        }
        else result = 0;

        //End logical mode
        if(result == count && flagged == redstonePowered)
            return;

        this.redstonePowered = flagged;
        count = result;
        blockEntity.notifyUpdate();
        notifyOutputs();
    }

    @Override
    public BulbState getBulbState() {
        return redstonePowered ? BulbState.RED : get() == IntOperationMode.MEMORY ? BulbState.GREEN : BulbState.DISABLED;
    }

    /* RENDER */

    /* DISPLAY LINK */
    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortened) {
        String text = shortened ? formatNumber(count) : String.valueOf(count);
        return Component.literal(text);
    }

    private static String formatNumber(int number){
        boolean negative = number < 0;
        number = Math.abs(number);
        if (number >= 1000000) return (negative ? "-":"") + String.format("%.1fM", number / 1000000f);
        if (number >=1000) return (negative ? "-":"") + String.format("%.1fK", number / 1000f);
        return (negative ? "-":"") + number;
    }
}
