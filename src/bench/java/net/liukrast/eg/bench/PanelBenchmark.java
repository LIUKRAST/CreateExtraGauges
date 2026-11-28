package net.liukrast.eg.bench;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.deployer.lib.logistics.board.ScrollOptionPanelBehaviour;
import net.liukrast.eg.content.logistics.board.FilterPanelBehaviour;
import net.liukrast.eg.content.logistics.board.LogicalMode;
import net.liukrast.eg.content.logistics.board.PassivePanelBehaviour;
import net.liukrast.eg.content.logistics.board.StringPanelBehaviour;
import net.liukrast.eg.registry.EGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
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
    private static final List<Runnable> preTick = new ArrayList<>();
    private static final List<Runnable> postTick = new ArrayList<>();
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
        if (!ENABLED) return;
        if (measuringScenario) preTick.forEach(Runnable::run);
        tickStart = System.nanoTime();
    }

    @SubscribeEvent
    public static void onTickEnd(ServerTickEvent.Post event) {
        if (!ENABLED) return;
        long duration = System.nanoTime() - tickStart;
        if (measuringScenario) postTick.forEach(Runnable::run);
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
            case "order" -> buildConnectionOrder(level);
            case "filter" -> buildFilterSwap(level);
            case "rewrite" -> buildAddressRewrite(level);
            case "barrel" -> buildBarrelRewrite(level);
            case "phase" -> buildPhaseProbe(level);
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

    private static void buildConnectionOrder(ServerLevel level) {
        PanelBlockItem stringItem = (PanelBlockItem) EGItems.STRING_GAUGE.get();
        var slots = FactoryPanelBlock.PanelSlot.values();
        var target = (StringPanelBehaviour) placeCustomPanel(level, new BlockPos(10, Y, 40), slots[0], stringItem);
        String expected = "HGFEDCBA";
        for (int i = 0; i < expected.length(); i++) {
            BlockPos pos = new BlockPos(10 + (i * 7) % 11 - 5, Y, 40 + (i * 5) % 9 - 4);
            if (pos.equals(target.getPanelPosition().pos())) pos = pos.above();
            var source = (StringPanelBehaviour) placeCustomPanel(level, pos, slots[i % 4], stringItem);
            source.setFilter("", "^$", String.valueOf(expected.charAt(i)));
            source.value = String.valueOf(expected.charAt(i));
            target.addConnection(source.getPanelPosition());
        }

        sanityChecks.add(() -> {
            target.notifiedFromInput();
            String live = target.value;
            CompoundTag tag = new CompoundTag();
            target.write(tag, level.registryAccess(), false);
            target.read(tag, level.registryAccess(), false);
            target.notifiedFromInput();
            report.add("  sanity: expected='" + expected + "', live='" + live + "', after reload='" + target.value + "'"
                    + (expected.equals(live) && expected.equals(target.value) ? " OK" : " MISMATCH"));
        });
    }

    private static void buildFilterSwap(ServerLevel level) {
        BlockPos chestPos = new BlockPos(0, Y, 60);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Items.OAK_PLANKS, 2));
            chest.setItem(1, new ItemStack(Items.BIRCH_PLANKS, 10));
        }
        BlockPos packagerPos = chestPos.above();
        level.setBlock(packagerPos, AllBlocks.PACKAGER.getDefaultState().setValue(PackagerBlock.FACING, Direction.UP), 3);
        BlockPos linkPos = packagerPos.east();
        level.setBlock(linkPos, AllBlocks.STOCK_LINK.getDefaultState()
                .setValue(PackagerLinkBlock.FACE, AttachFace.WALL)
                .setValue(PackagerLinkBlock.FACING, Direction.EAST), 3);
        if (level.getBlockEntity(linkPos) instanceof PackagerLinkBlockEntity link)
            link.behaviour.freqId = NETWORK;

        ItemStack listFilter = AllItems.FILTER.asStack();
        listFilter.set(AllDataComponents.FILTER_ITEMS, ItemContainerContents.fromItems(List.of(
                new ItemStack(Items.OAK_PLANKS), new ItemStack(Items.BIRCH_PLANKS))));

        var filter = (FilterPanelBehaviour) placeCustomPanel(level, new BlockPos(4, Y, 60), FactoryPanelBlock.PanelSlot.TOP_LEFT, (PanelBlockItem) EGItems.FILTER_GAUGE.get());
        filter.setFilter(listFilter);
        filter.ponder$setFilter(new ItemStack(Items.OAK_PLANKS));

        var target = placeVanillaPanel(level, new BlockPos(4, Y, 60), FactoryPanelBlock.PanelSlot.TOP_RIGHT);
        target.setFilter(new ItemStack(Items.STICK));
        target.count = 1;
        target.upTo = false;
        target.recipeAddress = "bench";
        target.addConnection(filter.getPanelPosition());
        target.targetedBy.get(filter.getPanelPosition()).amount = 4;
        target.activeCraftingArrangement = new ArrayList<>(List.of(new ItemStack(Items.OAK_PLANKS, 2), ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.OAK_PLANKS, 2)));
        FactoryPanelPosition filterPos = filter.getPanelPosition();
        FactoryPanelPosition targetPos = target.getPanelPosition();

        sanityChecks.add(() -> {
            var liveFilter = (FilterPanelBehaviour) FactoryPanelBehaviour.at(level, filterPos);
            var liveTarget = FactoryPanelBehaviour.at(level, targetPos);
            boolean swapped = liveFilter.getFilter().is(Items.BIRCH_PLANKS);
            boolean arrangement = liveTarget.activeCraftingArrangement.getFirst().is(Items.BIRCH_PLANKS);
            report.add("  sanity: filter gauge item=" + BuiltInRegistries.ITEM.getKey(liveFilter.getFilter().getItem())
                    + ", first arrangement slot=" + BuiltInRegistries.ITEM.getKey(liveTarget.activeCraftingArrangement.getFirst().getItem())
                    + (swapped && arrangement ? " OK" : " MISMATCH"));
        });
    }

    private static void buildAddressRewrite(ServerLevel level) {
        BlockPos chestPos = new BlockPos(0, Y, 80);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        ItemStack box = PackageItem.containing(List.of(new ItemStack(Items.DIAMOND, 3)));
        PackageItem.addAddress(box, "Alice");
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)
            chest.setItem(0, box);
        BlockPos packagerPos = chestPos.above();
        level.setBlock(packagerPos, AllBlocks.PACKAGER.getDefaultState().setValue(PackagerBlock.FACING, Direction.UP), 3);

        BlockPos panelPos = packagerPos.north();
        level.setBlock(panelPos, AllBlocks.FACTORY_GAUGE.getDefaultState()
                .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 3);
        var panel = (StringPanelBehaviour) placeCustomPanel(level, panelPos, FactoryPanelBlock.PanelSlot.TOP_LEFT, (PanelBlockItem) EGItems.STRING_GAUGE.get());
        panel.setFilter("", "Ali(.*)", "Bob$1");

        sanityChecks.add(() -> {
            if (!(level.getBlockEntity(packagerPos) instanceof PackagerBlockEntity packager)) {
                report.add("  sanity: packager missing MISMATCH");
                return;
            }
            packager.activate();
            String address = packager.heldBox.isEmpty() ? "<no package>" : PackageItem.getAddress(packager.heldBox);
            report.add("  sanity: factory logistics loaded=" + ModList.get().isLoaded("create_factory_logistics")
                    + ", rewriter interaction=" + panel.hasInteraction("rewriter")
                    + ", package address='" + address + "' (expected 'Bobce')"
                    + ("Bobce".equals(address) ? " OK" : " MISMATCH"));
        });
    }

    private static void buildBarrelRewrite(ServerLevel level) {
        BlockPos tankPos = new BlockPos(0, Y, 100);
        level.setBlock(tankPos, AllBlocks.FLUID_TANK.getDefaultState(), 3);
        BlockPos jarPackagerPos = tankPos.above();
        Block jarPackagerBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("create_factory_logistics:jar_packager"));
        boolean hasFL = jarPackagerBlock != Blocks.AIR;
        if (hasFL) {
            BlockState state = jarPackagerBlock.defaultBlockState();
            if (state.hasProperty(PackagerBlock.FACING)) state = state.setValue(PackagerBlock.FACING, Direction.UP);
            level.setBlock(jarPackagerPos, state, 3);
        }
        BlockPos a = placeBarrelStation(level, new BlockPos(6, Y, 100), true);
        BlockPos b = placeBarrelStation(level, new BlockPos(12, Y, 100), false);
        BlockPos c = placeBarrelStation(level, new BlockPos(18, Y, 100), true);

        sanityChecks.add(() -> {
            ItemStack jar = ItemStack.EMPTY;
            if (hasFL && level.getBlockEntity(tankPos) instanceof FluidTankBlockEntity tank) {
                tank.getTankInventory().fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);
                if (level.getBlockEntity(jarPackagerPos) instanceof PackagerBlockEntity jarPackager) {
                    jarPackager.activate();
                    jar = jarPackager.heldBox.copy();
                    jarPackager.heldBox = ItemStack.EMPTY;
                }
            }
            report.add("  sanity: FL jar created=" + (jar.isEmpty() ? "no" : jar.getItem() + " isPackage=" + PackageItem.isPackage(jar)));
            if (!jar.isEmpty()) PackageItem.addAddress(jar, "ciaoaoaoa");
            ItemStack itemBox = PackageItem.containing(List.of(new ItemStack(Items.DIAMOND, 3)));
            PackageItem.addAddress(itemBox, "ciaoaoaoa");
            runBarrelStation(level, "A jar+item, gauge", a, jar.copy(), itemBox.copy());
            runBarrelStation(level, "B jar+item, no gauge", b, jar.copy(), itemBox.copy());
            runBarrelStation(level, "C item only, gauge", c, ItemStack.EMPTY, itemBox.copy());
        });
    }

    private static BlockPos placeBarrelStation(ServerLevel level, BlockPos barrelPos, boolean withGauge) {
        level.setBlock(barrelPos, Blocks.BARREL.defaultBlockState(), 3);
        BlockPos packagerPos = barrelPos.above();
        level.setBlock(packagerPos, AllBlocks.PACKAGER.getDefaultState().setValue(PackagerBlock.FACING, Direction.UP), 3);
        if (withGauge) {
            BlockPos panelPos = packagerPos.north();
            level.setBlock(panelPos, AllBlocks.FACTORY_GAUGE.getDefaultState()
                    .setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL)
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 3);
            var panel = (StringPanelBehaviour) placeCustomPanel(level, panelPos, FactoryPanelBlock.PanelSlot.TOP_LEFT, (PanelBlockItem) EGItems.STRING_GAUGE.get());
            panel.setFilter("", "a", "b");
        }
        return packagerPos;
    }

    private static void runBarrelStation(ServerLevel level, String name, BlockPos packagerPos, ItemStack first, ItemStack second) {
        if (level.getBlockEntity(packagerPos.below()) instanceof BarrelBlockEntity barrel) {
            barrel.setItem(0, first);
            barrel.setItem(1, second);
        }
        if (!(level.getBlockEntity(packagerPos) instanceof PackagerBlockEntity packager)) {
            report.add("  " + name + ": packager missing");
            return;
        }
        packager.activate();
        ItemStack held = packager.heldBox;
        StringBuilder left = new StringBuilder();
        if (level.getBlockEntity(packagerPos.below()) instanceof BarrelBlockEntity barrel)
            for (int i = 0; i < barrel.getContainerSize(); i++)
                if (!barrel.getItem(i).isEmpty()) left.append(" ").append(barrel.getItem(i));
        report.add("  " + name + ": out=" + (held.isEmpty() ? "<nothing>" : held + " addr='" + PackageItem.getAddress(held) + "'")
                + " signAddress='" + packager.signBasedAddress + "' left in barrel:" + left);
    }

    /**
     * Checks that the end of a long chain of gauges follows its source in the same tick:
     * a chain fed by an oscillating NOR ring, and a chain fed by a gauge that flips once every 100 ticks.
     */
    private static void buildPhaseProbe(ServerLevel level) {
        PanelBlockItem logicItem = (PanelBlockItem) EGItems.LOGIC_GAUGE.get();
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
        FactoryPanelBehaviour ringEnd = chain(level, logicItem, slots, next, ring.getFirst(), 30);

        // A gauge with no inputs: NOR outputs true, OR outputs false
        var steady = placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], logicItem);
        ((ScrollOptionPanelBehaviour<?>) steady).value = LogicalMode.NOR.ordinal();
        FactoryPanelBehaviour steadyEnd = chain(level, logicItem, slots, next, steady, 30);

        // Create's own Factory Gauges powered through a redstone connection: powered while their input outputs true
        var ringVanilla = placeVanillaPanel(level, nextPos(next), slots[next[0]++ % 4]);
        ringVanilla.addConnection(ringEnd.getPanelPosition());
        var steadyVanilla = placeVanillaPanel(level, nextPos(next), slots[next[0]++ % 4]);
        steadyVanilla.addConnection(steadyEnd.getPanelPosition());
        FactoryPanelPosition rv = ringVanilla.getPanelPosition(), sv = steadyVanilla.getPanelPosition();
        List<Boolean> ringVan = new ArrayList<>(), steadyVan = new ArrayList<>();

        FactoryPanelPosition r0 = ring.getFirst().getPanelPosition(), re = ringEnd.getPanelPosition();
        FactoryPanelPosition s0 = steady.getPanelPosition(), se = steadyEnd.getPanelPosition();
        List<Boolean> ringSrc = new ArrayList<>(), ringOut = new ArrayList<>(), steadySrc = new ArrayList<>(), steadyOut = new ArrayList<>();
        int[] probeTick = {0};
        preTick.add(() -> {
            if (++probeTick[0] % 100 != 0) return;
            var live = (ScrollOptionPanelBehaviour<?>) FactoryPanelBehaviour.at(level, s0);
            live.value = live.value == LogicalMode.NOR.ordinal() ? LogicalMode.OR.ordinal() : LogicalMode.NOR.ordinal();
            live.checkForRedstoneInput();
        });
        postTick.add(() -> {
            ringSrc.add(output(level, r0));
            ringOut.add(output(level, re));
            steadySrc.add(output(level, s0));
            steadyOut.add(output(level, se));
            ringVan.add(FactoryPanelBehaviour.at(level, rv).redstonePowered);
            steadyVan.add(FactoryPanelBehaviour.at(level, sv).redstonePowered);
        });

        sanityChecks.add(() -> {
            report.add("  ring  -> chain of 30: " + lagReport(ringSrc, ringOut));
            report.add("  steady-> chain of 30: " + lagReport(steadySrc, steadyOut));
            report.add("  ring chain end -> Factory Gauge powered: " + lagReport(ringOut, ringVan));
            report.add("  steady chain end -> Factory Gauge powered: " + lagReport(steadyOut, steadyVan));
            int changes = 0;
            for (int i = 1; i < ringSrc.size(); i++) if (ringSrc.get(i) != ringSrc.get(i - 1)) changes++;
            report.add("  ring source changes=" + changes + " over " + ringSrc.size() + " ticks");
        });
    }

    private static FactoryPanelBehaviour chain(ServerLevel level, PanelBlockItem item, FactoryPanelBlock.PanelSlot[] slots, int[] next, FactoryPanelBehaviour from, int length) {
        FactoryPanelBehaviour previous = from;
        for (int i = 0; i < length; i++) {
            var panel = placeCustomPanel(level, nextPos(next), slots[next[0]++ % 4], item);
            panel.addConnection(previous.getPanelPosition());
            previous = panel;
        }
        return previous;
    }

    private static boolean output(ServerLevel level, FactoryPanelPosition pos) {
        return !FactoryPanelBehaviour.at(level, pos).redstonePowered;
    }

    private static String lagReport(List<Boolean> source, List<Boolean> out) {
        StringBuilder sb = new StringBuilder();
        for (int lag = 0; lag <= 3; lag++) {
            int same = 0, total = 0;
            for (int i = lag; i < out.size(); i++, total++) if (out.get(i).equals(source.get(i - lag))) same++;
            sb.append("lag").append(lag).append("=").append(same).append("/").append(total).append(" ");
        }
        List<Integer> misses = new ArrayList<>();
        for (int i = 0; i < out.size(); i++) if (!out.get(i).equals(source.get(i))) misses.add(i);
        if (!misses.isEmpty() && misses.size() < out.size() / 2)
            sb.append("lag0 misses at ticks ").append(misses.getFirst()).append("..").append(misses.getLast()).append(" (").append(misses.size()).append(") ");
        return sb.toString().trim();
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
