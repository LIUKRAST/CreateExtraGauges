package net.liukrast.eg.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.eg.ExtraGauges;

public class RegisterPartialModels {
    public static final PartialModel LOGIC_PANEL = block("logic_gauge/panel");

    private static PartialModel block(@SuppressWarnings("SameParameterValue") String path) {
        return PartialModel.of(ExtraGauges.id("block/" + path));
    }

    public static void init() {}
}
