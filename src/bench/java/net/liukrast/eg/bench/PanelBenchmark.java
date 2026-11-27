package net.liukrast.eg.bench;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.deployer.lib.logistics.board.ScrollOptionPanelBehaviour;
import net.liukrast.eg.content.logistics.board.LogicalMode;
import net.liukrast.eg.content.logistics.board.PassivePanelBehaviour;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@EventBusSubscriber(modid = "eg_bench")
public final class PanelBenchmark {
    private static final boolean ENABLED = Boolean.getBoolean("eg.bench");
    private static final String SCENARIO = System.getProperty("eg.bench.scenario", "stock");
    private static final String LABEL = System.getProperty("eg.bench.label", "unlabeled");
    private static final Path OUTPUT = Path.of(System.getProperty("eg.bench.out", "bench-results.txt"));
    private static final int WARMUP_TICKS = 200;
    private static final int MEASURED_TICKS = 1200;
    private static final int Y = -60;
    private static final UUID NETWORK = UUID.nameUUIDFromBytes("eg-bench".getBytes());

    private static final List<String> report = new ArrayList<>();
    private static final List<Runnable> sanityChecks = new ArrayList<>();
    private static boolean measuringScenario;
    private static int tick;
    private static long tickStart;
    private static long[] samples = new long[MEASURED_TICKS];

    private PanelBenchmark() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (!ENABLED) return;
        ServerLevel level = event.getServer().overworld();
        for (int cx = -2; cx <= 8; cx++)
            for (int cz = -2; cz <= 8; cz++)
                level.setChunkForced(cx, cz, true);
        tick = 0;
    }

    @SubscribeEvent
    public static void onTickStart(ServerTickEvent.Pre event) {
        if (ENABLED) tickStart = System.nanoTime();
    }

    @SubscribeEvent
    public static void onTickEnd(ServerTickEvent.Post event) {
        if (!ENABLED) return;
        long duration = System.nanoTime() - tickStart;
        tick++;
        int measuredIndex = tick - WARMUP_TICKS - 1;
        if (measuredIndex >= 0 && measuredIndex < MEASURED_TICKS) samples[measuredIndex] = duration;
        if (measuredIndex != MEASURED_TICKS - 1) return;

        MinecraftServer server = event.getServer();
        if (!measuringScenario) {
            report.add(summarize("idle", samples));
            buildScenario(server.overworld());
            measuringScenario = true;
            tick = 0;
            samples = new long[MEASURED_TICKS];
            return;
        }
        report.add(summarize(SCENARIO, samples));
        sanityChecks.forEach(Runnable::run);
        writeReport();
        server.halt(false);
    }

    private static void buildScenario(ServerLevel level) {
        switch (SCENARIO) {
            case "stock" -> buildStockNetwork(level);
            case "logic" -> buildLogicCascade(level);
            default -> throw new IllegalArgumentException("Unknown scenario " + SCENARIO);
        }
    }

    private static void buildStockNetwork(ServerLevel level) {
        List<Item> items = BuiltInRegistries.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .limit(16 * 27)
                .toList();

        for (int station = 0; station < 16; station++) {
            BlockPos chestPos = new BlockPos(station * 3, Y, 0);
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)
                for (int slot = 0; slot < 27; slot++) {
                    Item item = items.get(station * 27 + slot);
                    chest.setItem(slot, new ItemStack(item, new ItemStack(item).getMaxStackSize()));
                }
            BlockPos packagerPos = chestPos.above();
            level.setBlock(packagerPos, AllBlocks.PACKAGER.getDefaultState().setValue(PackagerBlock.FACING, Direction.UP), 3);
            BlockPos linkPos = packagerPos.east();
            level.setBlock(linkPos, AllBlocks.STOCK_LINK.getDefaultState()
                    .setValue(PackagerLinkBlock.FACE, AttachFace.WALL)
                    .setValue(PackagerLinkBlock.FACING, Direction.EAST), 3);
            if (level.getBlockEntity(linkPos) instanceof PackagerLinkBlockEntity link)
                link.behaviour.freqId = NETWORK;
        }

        List<FactoryPanelBehaviour> passive = new ArrayList<>();
        PanelBlockItem passiveItem = (PanelBlockItem) EGItems.PASSIVE_GAUGE.get();
        int index = 0;
        for (int block = 0; block < 10; block++)
            for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
                var panel = placeCustomPanel(level, new BlockPos(block * 2, Y, 4), slot, passiveItem);
                panel.setFilter(new ItemStack(items.get((index++ * 7) % items.size())));
                passive.add(panel);
            }

        List<FactoryPanelBehaviour> vanillaTargets = new ArrayList<>();
        for (int recipe = 0; recipe < 10; recipe++) {
            var target = placeVanillaPanel(level, new BlockPos(recipe * 2, Y, 8), FactoryPanelBlock.PanelSlot.TOP_LEFT);
            target.setFilter(new ItemStack(items.get(recipe)));
            target.count = 1;
            target.upTo = true;
            target.recipeAddress = "bench";
            for (int ingredient = 0; ingredient < 3; ingredient++) {
                var slot = FactoryPanelBlock.PanelSlot.values()[ingredient + 1];
                var source = placeVanillaPanel(level, new BlockPos(recipe * 2, Y, 8), slot);
                source.setFilter(new ItemStack(items.get(100 + recipe * 3 + ingredient)));
                target.addConnection(source.getPanelPosition());
            }
            vanillaTargets.add(target);
        }

        sanityChecks.add(() -> {
            int distinct = LogisticsManager.getSummaryOfNetwork(NETWORK, false).getStacks().size();
            long passiveWithStock = passive.stream().filter(p -> p.getLevelInStorage() > 0).count();
            long satisfiedTargets = vanillaTargets.stream().filter(p -> p.satisfied).count();
            long connected = vanillaTargets.stream().filter(p -> p.targetedBy.size() == 3).count();
            report.add("  sanity: distinct items on network=" + distinct
                    + ", passive gauges seeing stock=" + passiveWithStock + "/" + passive.size()
                    + ", vanilla targets satisfied=" + satisfiedTargets + "/" + vanillaTargets.size()
                    + ", vanilla targets with 3 inputs=" + connected + "/" + vanillaTargets.size());
        });
    }

    private static void buildLogicCascade(ServerLevel level) {
        PanelBlockItem logicItem = (PanelBlockItem) EGItems.LOGIC_GAUGE.get();
        PanelBlockItem stringItem = (PanelBlockItem) EGItems.STRING_GAUGE.get();
        var slots = FactoryPanelBlock.PanelSlot.values();
        int[] next = {0};

        List<FactoryPanelBehaviour> ring = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var panel = placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], logicItem);
            ((ScrollOptionPanelBehaviour<?>) panel).value = LogicalMode.NOR.ordinal();
            ring.add(panel);
        }
        for (int i = 0; i < 3; i++)
            ring.get((i + 1) % 3).addConnection(ring.get(i).getPanelPosition());

        List<FactoryPanelBehaviour> all = new ArrayList<>(ring);
        FactoryPanelBehaviour previous = ring.getFirst();
        for (int i = 0; i < 30; i++) {
            var panel = placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], logicItem);
            panel.addConnection(previous.getPanelPosition());
            all.add(panel);
            previous = panel;
        }

        List<FactoryPanelBehaviour> layer = List.of(previous);
        for (int depth = 0; depth < 3; depth++) {
            List<FactoryPanelBehaviour> nextLayer = new ArrayList<>();
            for (var parent : layer)
                for (int child = 0; child < 3; child++) {
                    var panel = placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], logicItem);
                    panel.addConnection(parent.getPanelPosition());
                    nextLayer.add(panel);
                }
            all.addAll(nextLayer);
            layer = nextLayer;
        }

        List<StringPanelBehaviour> strings = new ArrayList<>();
        FactoryPanelBehaviour stringSource = previous;
        for (int i = 0; i < 10; i++) {
            var panel = (StringPanelBehaviour) placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], stringItem);
            panel.addConnection(stringSource.getPanelPosition());
            panel.setFilter("", "(\\w+)", "$1!");
            strings.add(panel);
            stringSource = panel;
        }

        sanityChecks.add(() -> {
            long connected = all.stream().filter(p -> !p.targetedBy.isEmpty()).count();
            report.add("  sanity: logic panels=" + all.size() + ", with an input=" + connected
                    + ", string panels with value=" + strings.stream().filter(s -> s.value != null && !s.value.isEmpty()).count() + "/" + strings.size()
                    + ", last string value='" + strings.getLast().value + "'");
        });
    }

    private static BlockPos nextPos(int[] counter) {
        int block = counter[0] / 4;
        return new BlockPos((block % 20) * 2, Y, 20 + (block / 20) * 2);
    }

    private static FactoryPanelBlockEntity ensurePanelBlock(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof FactoryPanelBlockEntity)) {
            BlockState state = AllBlocks.FACTORY_GAUGE.getDefaultState()
                    .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR)
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            level.setBlock(pos, state, 3);
        }
        return (FactoryPanelBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos));
    }

    private static FactoryPanelBehaviour placeVanillaPanel(ServerLevel level, BlockPos pos, FactoryPanelBlock.PanelSlot slot) {
        var be = ensurePanelBlock(level, pos);
        be.addPanel(slot, NETWORK);
        return be.panels.get(slot);
    }

    private static FactoryPanelBehaviour placeCustomPanel(ServerLevel level, BlockPos pos, FactoryPanelBlock.PanelSlot slot, PanelBlockItem item) {
        var be = ensurePanelBlock(level, pos);
        item.applyToSlot(be, slot, NETWORK);
        return be.panels.get(slot);
    }

    private static String summarize(String name, long[] values) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        double mean = Arrays.stream(sorted).average().orElse(0) / 1e6;
        return String.format(Locale.ROOT, "[%s] %-6s mean=%.3fms p50=%.3fms p95=%.3fms p99=%.3fms max=%.3fms",
                LABEL, name, mean,
                sorted[sorted.length / 2] / 1e6,
                sorted[(int) (sorted.length * 0.95)] / 1e6,
                sorted[(int) (sorted.length * 0.99)] / 1e6,
                sorted[sorted.length - 1] / 1e6);
    }

    private static void writeReport() {
        report.forEach(line -> System.out.println("EG-BENCH " + line));
        try {
            Files.write(OUTPUT, report, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
