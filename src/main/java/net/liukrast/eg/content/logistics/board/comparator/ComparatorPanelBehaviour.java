package net.liukrast.eg.content.logistics.board.comparator;

import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.ScrollOptionPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

public class ComparatorPanelBehaviour extends ScrollOptionPanelBehaviour<ComparatorMode> {
    public int[] left = new int[0];
    public int[] right = new int[0];
    public ComparingOperator comparatorMode = ComparingOperator.EQUALS;
    public boolean advanced;

    //region Init
    public ComparatorPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(Component.translatable("create.logistics.comparator_value"), type, be, slot, ComparatorMode.class);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerOutput(DeployerPanelConnections.REDSTONE, () -> !redstonePowered);
        builder.registerInput(DeployerPanelConnections.NUMBERS);
        builder.registerOutput(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
    }

    @Override
    public Item getItem() {
        return EGItems.COMPARATOR_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.COMPARATOR_PANEL;
    }
    //endregion

    //region Value
    @Override
    public void setValue(int value) {
        boolean advanced = value == 1;
        if(advanced != this.advanced) {
            this.advanced = advanced;
            this.blockEntity.setChanged();
            this.blockEntity.sendData();
        }
    }

    @Override
    public int getValue() {
        return advanced ? 1 : 0;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        if(valueSetting.row() == 2) {
            int value = valueSetting.value();
            if (!valueSetting.equals(getValueSettings()))
                playFeedbackSound(this);
            if(value == this.value) return;
            this.value = value;
            blockEntity.setChanged();
            blockEntity.sendData();
            return;
        } else if(valueSetting.row() == 1) {
            comparatorMode = ComparingOperator.values()[Mth.clamp(valueSetting.value(), 0, 5)];
            checkForRedstoneInput();
            return;
        }
        super.setValueSettings(player, valueSetting, ctrlHeld);
    }

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(0, this.advanced ? 1 : 0);
    }

    //endregion

    // region Data
    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putInt("ComparatorMode", comparatorMode.ordinal());
        nbt.putBoolean("Advanced", advanced);
        nbt.putIntArray("Right", right);
        nbt.putIntArray("Left", left);

    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        comparatorMode = ComparingOperator.values()[nbt.getInt("ComparatorMode")];
        advanced = nbt.getBoolean("Advanced");

        right = nbt.getIntArray("Right");
        left = nbt.getIntArray("Left");

        List<FactoryPanelConnection> allConnections = new ArrayList<>();
        allConnections.addAll(targetedBy.values());
        allConnections.addAll(targetedByLinks.values());
        allConnections.addAll(getTargetedByExtra().values());

        Set<Integer> seen = new HashSet<>();
        boolean anyDuplicate = false;
        for (var conn : allConnections) {
            if (!seen.add(conn.amount)) {
                anyDuplicate = true;
                break;
            }
        }

        if (anyDuplicate) {
            for (int i = 0; i < allConnections.size(); i++) {
                allConnections.get(i).amount = i;
            }
        }
    }

    @Override
    public void onConnectionAdded(FactoryPanelConnection connection) {
        var link = linkAt(getWorld(), connection);
        if(link != null && !link.isOutput()) return;
        Set<Integer> usedAmounts = new HashSet<>();
        for (FactoryPanelConnection current : Stream.concat(
                Stream.concat(
                        targetedBy.values().stream(),
                        targetedByLinks.values().stream()
                ),
                getTargetedByExtra().values().stream()
        ).toList()) {
            if (current == connection) continue;
            var link1 = linkAt(getWorld(), current);
            if(link1 != null && !link1.isOutput()) continue;
            usedAmounts.add(current.amount);
        }

        int mex = 0;
        while (usedAmounts.contains(mex)) mex++;
        connection.amount = mex;
    }
    //endregion

    //region logic
    @Override
    public BulbState getBulbState() {
        return redstonePowered ? BulbState.RED : BulbState.DISABLED;
    }

    @Override
    public void notifiedFromInput() {
        if(!active)
            return;

        boolean shouldPower;
        if(advanced) {
            List<ConnectionValue<Float>> result = getAllValuesWithSource(DeployerPanelConnections.NUMBERS.get());
            if(result == null) return;
            float totLeft = 0;
            float totRight = 0;
            for(var conn : result) {
                if(ArrayUtils.contains(right, conn.connection().amount)) totRight+=conn.value();
                if(ArrayUtils.contains(left, conn.connection().amount)) totLeft+=conn.value();
            }
            shouldPower = comparatorMode
                    .test(totLeft, totRight);
        } else {

            List<Float> result = getAllValues(DeployerPanelConnections.NUMBERS.get());
            if (result == null) return;
            float res = result.stream().reduce(0f, Float::sum);

            shouldPower = comparatorMode
                    .test(res, value);
        }
        //End logical mode
        if (shouldPower != redstonePowered) return;
        redstonePowered = !shouldPower;
        blockEntity.notifyUpdate();
        for (FactoryPanelPosition panelPos : targeting) {
            if (!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if (behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        notifyOutputs();
    }
    //endregion

    //region Display
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new ComparatorPanelScreen(this));
    }
    //endregion

    //region Advanced
    public Triple<int[], int[], int[]> getInputs() {
        List<ConnectionValue<Float>> inputs = getAllValuesWithSource(DeployerPanelConnections.NUMBERS.get());
        if(inputs == null) return new Triple<>(new int[0], new int[0], new int[0]);
        List<Integer> collector = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        List<Integer> left = new ArrayList<>();
        block:
        for(var val : inputs) {
            var conn = val.connection();
            for(int i : this.left) {
                if(conn.amount == i) {
                    left.add(i);
                    continue block;
                }
            }
            for(int i : this.right) {
                if(conn.amount == i) {
                    right.add(i);
                    continue block;
                }
            }
            collector.add(conn.amount);
        }
        return new Triple<>(
                collector.stream().mapToInt(i -> i).toArray(),
                right.stream().mapToInt(i -> i).toArray(),
                left.stream().mapToInt(i -> i).toArray()
        );
    }
    //endregion
}
