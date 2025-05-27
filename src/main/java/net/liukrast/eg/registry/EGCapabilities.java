package net.liukrast.eg.registry;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class EGCapabilities {
    private EGCapabilities() {}

    public static void registerDefaults(RegisterCapabilitiesEvent event) {
        var validBlocks = new Block[]{Blocks.LEVER, AllBlocks.ANALOG_LEVER.get()};

        event.registerBlock(
                EGPanelConnections.REDSTONE.get().asCapability(),
                (level, blockPos, blockState, blockEntity, ctx) -> {
                    if(ctx == null) return null;
                    if(ctx.matches(blockState)) return blockState.getSignal(level, blockPos, Direction.NORTH); //TODO: Replace with better case check
                    return null;
                },
                validBlocks
        );

        event.registerBlock(
                EGPanelConnections.INTEGER.get().asCapability(),
                (level, blockPos, blockState, blockEntity, ctx) -> {
                    if(ctx == null) return null;
                    if(ctx.matches(blockState)) return blockState.getSignal(level, blockPos, Direction.NORTH); //TODO: Replace with better case check
                    return null;
                },
                validBlocks
        );
    }
}
