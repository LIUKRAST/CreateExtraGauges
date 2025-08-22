package net.liukrast.eg;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ExtraGaugesConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue LOGIC_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a logic gauge can update itself in a single tick")
            .defineInRange("logicGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue INT_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times an int gauge can update itself in a single tick")
            .defineInRange("intGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue COMPARATOR_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a comparator gauge can update itself in a single tick")
            .defineInRange("comparatorGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue COUNTER_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a counter gauge can update itself in a single tick")
            .defineInRange("counterGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue STRING_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a string gauge can update itself in a single tick")
            .defineInRange("stringGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue STRING_MAX_LENGTH = BUILDER
            .comment("Defines the max length of a string collected by a string gauge. Increase at your own risk")
            .defineInRange("stringGaugeMaxLength", 256, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
    
    private static final ModConfigSpec.Builder CLIENT = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue PANEL_CACHING = CLIENT
            .comment("Caching all input values to display when non instant updates are still processing")
            .define("panelCaching", true);

    static final ModConfigSpec CLIENT_SPEC = CLIENT.build();
}
