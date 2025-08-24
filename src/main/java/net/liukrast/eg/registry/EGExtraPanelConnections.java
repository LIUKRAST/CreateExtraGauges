package net.liukrast.eg.registry;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.Arrays;
import java.util.Optional;

public class EGExtraPanelConnections {
    private EGExtraPanelConnections() {}

    public static void register() {
        Block[] levers = {Blocks.LEVER, AllBlocks.ANALOG_LEVER.get()};
        EGPanelConnections.REDSTONE.get().addListener((level, state, pos, be) -> Optional.of(state.getSignal(level, pos, Direction.NORTH)), levers);
        EGPanelConnections.INTEGER.get().addListener((level, state, pos, be) -> Optional.of(state.getSignal(level, pos, Direction.NORTH)), levers);
        EGPanelConnections.STRING.get().addListener((level, state, pos, be) -> {
            if(!(be instanceof SignBlockEntity sign)) return Optional.empty();
            return Optional.of(String.join("", Arrays.stream(sign.getFrontText().getMessages(false)).map(Component::getString).toArray(String[]::new)));
        }, BuiltInRegistries.BLOCK.stream().filter(b -> b instanceof WallSignBlock).toArray(Block[]::new));
    }
}
