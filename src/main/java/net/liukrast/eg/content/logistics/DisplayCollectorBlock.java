package net.liukrast.eg.content.logistics;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import net.liukrast.eg.registry.EGBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class DisplayCollectorBlock extends DisplayLinkBlock {

    public DisplayCollectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends DisplayCollectorBlockEntity> getBlockEntityType() {
        return EGBlockEntityTypes.DISPLAY_COLLECTOR.get();
    }
}
