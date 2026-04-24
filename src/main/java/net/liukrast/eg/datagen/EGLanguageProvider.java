package net.liukrast.eg.datagen;

import net.liukrast.deployer.lib.helper.datagen.DeployerLanguageProviderImpl;
import net.liukrast.eg.ExtraGauges;
import net.liukrast.eg.registry.EGBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

public class EGLanguageProvider extends DeployerLanguageProviderImpl {
    public EGLanguageProvider(PackOutput output) {
        super(output, ExtraGauges.CONSTANTS.getModId(), "en_us");
    }

    @Override
    protected void addTranslations() {
        addReplaced("itemGroup.%s", "Create: Extra Gauges");

        ExtraGauges.CONSTANTS.getElementEntries(BuiltInRegistries.ITEM)
                .forEach(e -> add(e.getValue().getDescriptionId(), autoName(e.getKey())));

        addShiftSummary(EGBlocks.LINKED_LEVER.asItem(), "Works like a redstone link but includes a lever. Connects to gauges and send an update instantly");
        addShiftSummary(EGBlocks.LINKED_BUTTON.asItem(), "Works like a redstone link but includes a lever. Connects to gauges and send an update instantly");

        addShiftSummary(EGBlocks.REDSTONE_PORT.asItem(), "Connects real-world redstone to gauges (only ON/OFF)")
                .addLine("When Wrenched", "Switches from input to output and viceversa");

        addShiftSummary(EGBlocks.ROSE_QUARTZ_PORT.asItem(), "Connects real-world redstone to gauges (Number connection, 0-15)")
                .addLine("When Wrenched", "Switches from input to output and viceversa");

        add("create.logistics.logic_gate", "Logic Gate");
        add("create.logistics.int_operation", "Int Operation");
        add("create.logistics.comparator_value", "Select the comparator mode");
        add("create.logistics.counter_threshold", "Choose the threshold");
        add("create.logistics.int_selection", "Select the integer value");

        add("logic_gauge.gate.or", "OR (Activate if any inputs are active)");
        add("logic_gauge.gate.and", "AND (Activate if all inputs are active)");
        add("logic_gauge.gate.nor", "NOR (Activate if all inputs are inactive)");
        add("logic_gauge.gate.nand", "NAND (Activate if any inputs are inactive)");
        add("logic_gauge.gate.xor", "XOR (Activate if active inputs are odd)");
        add("logic_gauge.gate.xnor", "XNOR (Activate if active inputs are even)");

        add("integer_gauge.mode.add", "ADD (Sums all inputs)");
        add("integer_gauge.mode.subtract", "SUBTRACT (Sums all inputs but gives negative result)");
        add("integer_gauge.mode.multiply", "MULTIPLY (Multiplies all inputs)");
        add("integer_gauge.mode.memory", "MEMORY (Saves sum of inputs if redstone is off)");

        add("comparator_gauge.mode.static", "STATIC (Compares a static value to sum of inputs)");
        add("comparator_gauge.mode.advanced", "ADVANCES (Let's you control which inputs are compared to which)");
        add("comparator_gauge.mode.equals", "Input equals constant");
        add("comparator_gauge.mode.different", "Input differs constant");
        add("comparator_gauge.mode.greater", "Input is greater than constant");
        add("comparator_gauge.mode.greater_equals", "Input is greater than or equals constant");
        add("comparator_gauge.mode.less", "Input is less than constant");
        add("comparator_gauge.mode.less_equals", "Input is less than or equals constant");

        add("comparator_gauge.info.title", "Drag'n drop");
        add("comparator_gauge.info.line_1", "Visualize inputs outside with a wrench,");
        add("comparator_gauge.info.line_2", "then drag them on one side of the equation");

        add("expression_gauge.info.title", "What are variables?");
        add("expression_gauge.info.line_1", "Each connection coming into the gauge");
        add("expression_gauge.info.line_2", "is a unique number you can use for your equations.");
        add("expression_gauge.info.line_3", "Hover a connection with a wrench to see what variable you're looking at");

        add("create.string_panel.input_in_rewrite_mode", "Input gauge cannot be in rewrite mode");
        add("create.display_collector.set", "Source position selected");
        add("create.display_collector.success", "Successfully bound to source position");

        add("filter_panel.no_filter", "No filter selected");
        add("filter_panel.active", "Filter panel active");

        add("extra_gauges.gui.string_panel.join", "Join:");
        add("extra_gauges.gui.string_panel.regex", "Regex:");
        add("extra_gauges.gui.string_panel.replacement", "Replacement:");

        add("extra_gauges.counter_panel.no_limit", "No limit");

        add("extra_gauges.gui.filter_panel.eject", "Eject filter");

        add("extra_gauges.gui.variable_connection.info_header", "Selected connection");
        add("extra_gauges.gui.variable_connection.name", "Variable:");

        add("extra_gauges.gui.factory_panel.crafting_input_tip_1", "mechanical crafters (%s blocks wide)");
        add("extra_gauges.gui.factory_panel.crafting_input_tip_2", "Minimum height: %s blocks");
        add("extra_gauges.gui.factory_panel.auto_crafting_input", "Recipe too big to render");
        add("extra_gauges.gui.factory_panel.extend_width", "Extend width (%s)");
        add("extra_gauges.gui.factory_panel.reduce_width", "Reduce width (%s)");
        add("extra_gauges.gui.passive_panel.no_recipe", "Missing recipe");

        // PASSIVE & EXTENDED
        createPonder(
                "passive_gauge", "Using Passive Gauges to optimize Automated Recipes",
                "Passive gauges work just like §9Factory §9gauges§r...",
                "...but you dont need to set the §9amount §rof items to produce.",
                "When sticks are missing...",
                "...passive gauge will try to keep in storage just the §9right §9amount§r.",
                "Once the first recipe starts crafting, the passive gauge automatically §cstops§r."
        );

        createPonder(
                "expanded_factory_recipes", "Crafting items on larger auto-crafters",
                "§9Extra §9gauges §rnow supports larger recipes to be crafted with factory gauges.",
                "Larger auto crafter setups are obviously needed.",
                "But smaller recipes can also match larger auto crafter setups, by increasing the §9width §rin the §eGUI§r.",
                "Don't get scared about your setup height, as soon as it matches the minimum required for the recipe, you're good to go!"
        );

        createPonder(
                "logic_gauge", "Transmit redstone with the logic gauge",
                "Logic gauges can transmit redstone through it's connections...",
                "...click on the Logic panel to open its menu...",
                "...press [+] to add a connection...",
                "...and select another gauge.",
                "The connection can be removed by left-clicking it with a wrench.",

                "Some blocks can transmit redstone to gauges, some can extract it",
                "The same [+] icon in the menu can be used to connect to those...",
                "...but the arrow direction is decided by the output",

                "By holding right-click, you can change the logic gate...",
                "...and when redstone starts flowing in the gauge...",
                "...it decides whether to output redstone or not."
        );

        createPonder(
                "integer_gauge", "Transmit integers with the integer gauge",
                "Int gauges can transmit integers §7(non-decimal numbers)§r through it's connections...",
                "...click on the int panel to open its menu...",
                "...press [+] to add a connection...",
                "...and select another gauge.",
                "The connection can be removed by left-clicking it with a wrench.",

                "Some blocks can transmit integers to gauges, some can extract them",
                "The same [+] icon in the menu can be used to connect to those...",
                "...but the arrow direction is decided by the output",

                "By holding right-click, you can change the operation...",
                "...and when numbers starts flowing in the gauge...",
                "All operations are calculated on integers, so the result will not have any decimal part",
                "A special operation, memory, will be covered in the next ponder",

                "When a redstone connection points to an int gauge...",
                "...the output will immediately be cancelled, and 0 is returned"
        );

        createPonder(
                "integer_gauge_memory", "Memorize numbers with the integer gauge",
                "Int gauges have a special memory operation",
                "When the number flowing into the gauge changes, the gauge instantly memorizes it...",
                "...but when redstone is flowing inside the gauge...",
                "...any change is not updated, and the gauge will now store the number forever...",
                "...until unlocked again."
        );

        createPonder(
                "comparator_gauge", "Compare numbers with the comparator gauge",
                "First, make sure the comparator gauge is receiving numbers...",
                "...then open the menu to set the constant and the comparison operator.",
                "The comparator gauge will now check if the input matches your condition...",
                "...and output redstone.",
                "Hold right-click to switch into advanced mode",
                "When connecting multiple connections, each one will have a name.",
                "You can check the name by holding a wrench on the connection",
                "The two connections are now available in a new GUI.",
                "You can drag'n drop the connections on each side of the operator...",
                "a < b"
        );

        createPonder(
                "counter_gauge", "Count inputs with the counter gauge",
                "Counter gauge will well... count redstone inputs!",
                "...because we all know you can't even count to 10.",
                "Every time a redstone input arrives to the gauge...",
                "...it will add 1.",
                "Hold right-click to set the threshold...",
                "...when the count reaches the threshold...",
                "...the gauge will transmit redstone.",
                "Counter gauge will also output the current count to panels reading numbers"
        );

        createPonder(
                "string_gauge", "Format text with the string gauge",
                "String gauges will transfer text over it's connections",
                "Open the menu, click on the [+] icon...",
                "...and click on a source you want to read text from.",
                "Other gauges also transfer text.",
                "✖ False",
                "0",
                "Open the menu and select a join value...",
                "...the gauge will now join all the inputs in one sentence",
                "Select a regex and a replacement...",
                "...the gauge will check for text matching the regex expression...",
                "...and replace every occurrence with the replacement.",
                "String gauge can also export redstone and number data...",
                "When the text is similar to a number, it will automatically be converted.",
                "Same goes for redstone."
        );

        createPonder(
                "expression_gauge", "Calculate expressions with the Expression gauge",
                "Complex expressions can be evaluated with an expression gauge",
                "Open the menu and click [+] to add a connection...",
                "...then press on the panel you want to get data from.",
                "Some other panels can instead read data from your gauge.",
                "Each connection flowing in the expression gauge will have it's own name...",
                "Set-up a mathematical expression inside the gauge...",
                "a + log(b) - sqrt(2)",

                "When a redstone connection points to an expression gauge gauge...",
                "...the output will immediately be cancelled, and 0 is returned"
        );

        createPonder(
                "filter_gauge", "Simplify recipes with the Filter gauge",
                "Filter gauges can be used as input for factory gauges...",
                "...to find multiple candidates for a recipe.",
                "Add a filter with all the items that can be used in your recipe...",
                "...and put at least 1 item matching the filter in your network",
                "You can now connect the factory gauge, open the menu and setup the recipe.",
                "When 8 oak planks are missing...",
                "...it will try to look up for other items matching the filter...",
                "...and update the recipe."
        );
    }
}
