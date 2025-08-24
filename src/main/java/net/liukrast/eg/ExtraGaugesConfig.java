package net.liukrast.eg;


import net.minecraftforge.common.ForgeConfigSpec;

public class ExtraGaugesConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue LOGIC_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a logic gauge can update itself in a single tick")
            .defineInRange("logicGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue INT_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times an int gauge can update itself in a single tick")
            .defineInRange("intGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue COMPARATOR_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a comparator gauge can update itself in a single tick")
            .defineInRange("comparatorGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue COUNTER_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a counter gauge can update itself in a single tick")
            .defineInRange("counterGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue STRING_MAX_CHAIN = BUILDER
            .comment("Defines the max amount of times a string gauge can update itself in a single tick")
            .defineInRange("stringGaugeMaxChain", 15,0, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue STRING_MAX_LENGTH = BUILDER
            .comment("Defines the max length of a string collected by a string gauge. Increase at your own risk")
            .defineInRange("stringGaugeMaxLength", 256, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue PANEL_CACHING = CLIENT
            .comment("Caching all input values to display when non instant updates are still processing")
            .define("panelCaching", true);

    static final ForgeConfigSpec CLIENT_SPEC = CLIENT.build();
}
