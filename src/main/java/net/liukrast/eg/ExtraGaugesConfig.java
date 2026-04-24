package net.liukrast.eg;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ExtraGaugesConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue STRING_MAX_LENGTH = BUILDER
            .comment("Defines the max length of a string collected by a string gauge. Increase at your own risk")
            .defineInRange("stringGaugeMaxLength", 256, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> STRING_TO_FLOAT_REGEX = BUILDER
            .comment("Defines the regex used by string gauge to convert from string to number")
            .define("stringGaugeToFloatRegex", "-?\\d+(\\.\\d+)?");

    public static final ModConfigSpec.ConfigValue<String> STRING_TO_REDSTONE_REGEX = BUILDER
            .comment("Defines the regex used by string gauge to convert from string to redstone")
            .define("stringGaugeToRedstoneRegex", "\\s*(true|1|on|yes|active|y)\\s*");

    static final ModConfigSpec SPEC = BUILDER.build();
}
