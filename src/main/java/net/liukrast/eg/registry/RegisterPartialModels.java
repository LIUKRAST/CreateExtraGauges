package net.liukrast.eg.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.ExtraGauges;

public class RegisterPartialModels {
    public static final PartialModel LOGIC_PANEL = block("logic_gauge");
    public static final PartialModel INT_PANEL = block("integer_gauge");
    public static final PartialModel COMPARATOR_PANEL = block("comparator_gauge");
    public static final PartialModel COUNTER_PANEL = block("counter_gauge");

    private static PartialModel block(String path) {
        return PartialModel.of(ExtraGauges.id("block/" + path));
    }

    public static void init() {}
}
