package net.liukrast.eg.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.EGConstants;
import org.jetbrains.annotations.ApiStatus;

public class EGPartialModels {
    private EGPartialModels() {}
    public static final PartialModel LOGIC_PANEL = block("logic_gauge");
    public static final PartialModel INT_PANEL = block("integer_gauge");
    public static final PartialModel COMPARATOR_PANEL = block("comparator_gauge");
    public static final PartialModel COUNTER_PANEL = block("counter_gauge");
    public static final PartialModel PASSIVE_PANEL = block("passive_gauge");
    public static final PartialModel STRING_PANEL = block("string_gauge");

    private static PartialModel block(String path) {
        return PartialModel.of(EGConstants.id("block/" + path));
    }

    @ApiStatus.Internal
    public static void init() {}
}
