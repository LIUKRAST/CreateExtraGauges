package net.liukrast.eg.content.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.gui.ScreenOpener;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnectionBuilder;
import net.liukrast.deployer.lib.registry.DeployerPanelConnections;
import net.liukrast.eg.registry.EGItems;
import net.liukrast.eg.registry.EGPartialModels;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionPanelBehaviour extends AbstractPanelBehaviour {
    public static final List<Function> FUNCTIONS = List.of(
            new Function("and", 2) {
                @Override
                public double apply(double... args) {
                    return (int) args[0] & (int) args[1];
                }
            },
            new Function("or", 2) {
                @Override
                public double apply(double... args) {
                    return (int) args[0] | (int) args[1];
                }
            },
            new Function("xor", 2) {
                @Override
                public double apply(double... args) {
                    return (int) args[0] ^ (int) args[1];
                }
            },
            new Function("not", 1) {
                @Override
                public double apply(double... args) {
                    return ~(int) args[0];
                }
            },
            new Function("nor", 2) {
                @Override
                public double apply(double... args) {
                    return ~((int) args[0] | (int) args[1]);
                }
            },
            new Function("nand", 2) {
                @Override
                public double apply(double... args) {
                    return ~((int) args[0] & (int) args[1]);
                }
            },
            new Function("xnor", 2) {
                @Override
                public double apply(double... args) {
                    return ~((int) args[0] ^ (int) args[1]);
                }
            },
            new Function("shl", 2) {
                @Override
                public double apply(double... args) {
                    return (int) args[0] << (int) args[1];
                }
            },
            new Function("shr", 2) {
                @Override
                public double apply(double... args) {
                    return (int) args[0] >> (int) args[1];
                }
            },
            new Function("ushr", 2) {
                @Override
                public double apply(double... args) {
                    return (int)args[0] >>> (int)args[1];
                }
            },
            new Function("imply", 2) {
                @Override
                public double apply(double... args) {
                    return ~(int)args[0] | (int)args[1];
                }
            },
            new Function("nimply", 2) {
                @Override
                public double apply(double... args) {
                    return (int)args[0] & ~ (int)args[1];
                }
            }
    );


    private float output;
    private String numberExpression = "";

    public ExpressionPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public void addConnections(PanelConnectionBuilder builder) {
        builder.registerBoth(DeployerPanelConnections.NUMBERS, () -> output);
        builder.registerInput(DeployerPanelConnections.REDSTONE);
        builder.registerOutput(DeployerPanelConnections.STRING.get(), () -> getDisplayLinkComponent(false).getString());
    }

    @Override
    public Item getItem() {
        return EGItems.EXPRESSION_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return EGPartialModels.EXPRESSION_PANEL;
    }

    @Override
    public void easyWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyWrite(nbt, registries, clientPacket);
        nbt.putFloat("Output", output);
        nbt.putString("Expression", numberExpression);
    }

    @Override
    public void easyRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.easyRead(nbt, registries, clientPacket);
        output = nbt.getFloat("Output");
        numberExpression = nbt.getString("Expression");
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

    @Override
    public void notifiedFromInput() {
        super.notifiedFromInput();
        List<Boolean> flagList = getAllValues(DeployerPanelConnections.REDSTONE.get());
        if(flagList == null) return;

        boolean flagged = flagList.stream().anyMatch(bl -> bl);

        float result;
        if(!flagged) {
            List<ConnectionValue<Float>> inputs = getAllValuesWithSource(DeployerPanelConnections.NUMBERS.get());
            if(inputs == null) return;

            try {
                Map<String, Double> variables = inputs.stream().collect(Collectors.toMap(
                        f -> String.valueOf((char) ('a' + f.connection().amount)),
                        key -> (double) key.value(),
                        Double::sum
                ));


                Expression expression = new ExpressionBuilder(numberExpression)
                        .variables(variables.keySet())
                        .functions(FUNCTIONS)
                        .build();

                variables.forEach(expression::setVariable);

                if (expression.validate().isValid()) {
                    result = (float) expression.evaluate();
                } else result = 0;
            } catch (Exception ignored) {
                result = 0;
            }
        } else result = 0;
        if(Math.abs(result - output) < 1e-6f) return;
        redstonePowered = flagged;
        output = result;
        blockEntity.notifyUpdate();
        notifyOutputs();
    }

    public void setFilter(String numberExpression) {
        this.numberExpression = numberExpression;
        blockEntity.notifyUpdate();
        notifiedFromInput();
    }

    @Override
    public MutableComponent getDisplayLinkComponent(boolean shortVersion) {
        DecimalFormat df = new DecimalFormat("###.##");
        return Component.literal(df.format(output));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new ExpressionPanelScreen(this));
    }

    public char[] getInputs() {
        List<ConnectionValue<Float>> inputs = getAllValuesWithSource(DeployerPanelConnections.NUMBERS.get());
        if(inputs == null) return new char[0];
        return inputs.stream()
                .map(f -> (char)('a' + f.connection().amount))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
                .toCharArray();
    }

    @Override
    public BulbState getBulbState() {
        return redstonePowered ? BulbState.RED : BulbState.DISABLED;
    }

    public String getExpression() {
        return numberExpression;
    }
}
