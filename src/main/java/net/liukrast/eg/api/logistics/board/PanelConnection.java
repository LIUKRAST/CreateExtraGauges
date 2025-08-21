package net.liukrast.eg.api.logistics.board;

import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.util.ConnectionExtra;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * */
public class PanelConnection<T> {
    private final Map<Block, ConnectionExtra<T>> extraConnections = new HashMap<>();

    public PanelConnection() {}

    public void addListener(ConnectionExtra<T> supplier, Block... validBlocks) {
        if(validBlocks.length == 0) ExtraGauges.LOGGER.error("Registered panel connection listener without any blocks. {}", this);
        for(var block : validBlocks) {
            extraConnections.put(block, supplier);
        }
    }

    public static Direction makeContext(BlockState state) {
        if(state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
            var attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);
            return switch (attachFace) {
                case CEILING -> Direction.UP;
                case FLOOR -> Direction.DOWN;
                case WALL -> state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            };
        } else if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        else if(state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING);
        return null;
    }

    public ConnectionExtra<T> getListener(Block block) {
        return extraConnections.get(block);
    }

    @Override
    public String toString() {
        var id = EGRegistries.PANEL_CONNECTION_REGISTRY.getKey(this);
        return id == null ? "unregistered" : id.toString();
    }
}
