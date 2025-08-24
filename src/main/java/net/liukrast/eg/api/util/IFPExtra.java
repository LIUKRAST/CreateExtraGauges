package net.liukrast.eg.api.util;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface IFPExtra {

    Map<BlockPos, FactoryPanelConnection> extra_gauges$getExtra();
    int extra_gauges$getWidth();
    void extra_gauges$setWidth(int width);
}
