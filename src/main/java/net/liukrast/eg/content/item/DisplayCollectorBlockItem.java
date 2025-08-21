package net.liukrast.eg.content.item;

import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.level.block.Block;

public class DisplayCollectorBlockItem extends ClickToLinkBlockItem {
    public DisplayCollectorBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public int getMaxDistanceFromSelection() {
        return AllConfigs.server().logistics.displayLinkRange.get();
    }

    @Override
    public String getMessageTranslationKey() {
        return "display_collector";
    }
}
