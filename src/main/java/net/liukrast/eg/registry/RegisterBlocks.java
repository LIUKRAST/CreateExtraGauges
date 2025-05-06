package net.liukrast.eg.registry;

import com.simibubi.create.foundation.data.SharedProperties;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.content.block.logic.LogicGaugeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExtraGauges.MOD_ID);

    public static final DeferredBlock<LogicGaugeBlock> LOGIC_GAUGE = BLOCKS.register("logic_gauge", () -> new LogicGaugeBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.copperMetal())));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
