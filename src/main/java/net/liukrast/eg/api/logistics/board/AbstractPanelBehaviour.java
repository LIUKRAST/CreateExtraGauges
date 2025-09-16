package net.liukrast.eg.api.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.*;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.liukrast.eg.api.EGRegistries;
import net.liukrast.eg.api.registry.PanelType;
import net.liukrast.eg.api.util.IFPExtra;
import net.liukrast.eg.mixin.FactoryPanelBehaviourIMixin;
import net.liukrast.eg.mixin.FilteringBehaviourMixin;
import net.liukrast.eg.registry.EGPanelConnections;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h1>Abstract Panel Behaviour</h1>
 * Allows creating custom panel behaviors<br>
 * */
public abstract class AbstractPanelBehaviour extends FactoryPanelBehaviour {
    private final PanelType<?> type;
    private final Reference2ObjectArrayMap<PanelConnection<?>, Supplier<?>> connections = new Reference2ObjectArrayMap<>();

    protected static final int WAITING = 0xffd541;
    protected static final int DISABLED = 0x888898;

    /**
     * This constructor allows to modify the valueBoxTransform to make a custom input system
     * */
    public AbstractPanelBehaviour(ValueBoxTransform valueBoxTransform, PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        this(type, be, slot);
        ((FilteringBehaviourMixin)this).setValueBoxTransform(valueBoxTransform);
    }

    public AbstractPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(be, slot);
        var builder = new PanelConnectionBuilder();
        addConnections(builder);
        connections.putAll(builder.map);
        this.type = type;
    }

    /**
     * Adds a new connection provider to this gauge.
     * This means other gauges can read information from this gauge.
     * Also keep in mind that the order these connections is added is important for some panels which read multiple connections
     * */
    public abstract void addConnections(PanelConnectionBuilder builder);

    /**
     * @return the connections set, sorted
     * */
    public Set<PanelConnection<?>> getConnections() {
        return connections.keySet();
    }

    /**
     * @return Whether the panel has a precise connection, using forge's deferred holder
     * */
    public <T> boolean hasConnection(RegistryObject<PanelConnection<T>> connection) {
        return hasConnection(connection.get());
    }

    /**
     * @return Whether this behavior has a precise connection
     * */
    public boolean hasConnection(PanelConnection<?> connection) {
        return connections.containsKey(connection);
    }

    /**
     * @param shortenNumbers whether the display is in mode "shortened" or "full_number"
     * @return The component for display links
     * */
    public MutableComponent getDisplayLinkComponent(boolean shortenNumbers) {
        return Component.empty();
    }

    /**
     * Whether the panel should skip calling {@link FactoryPanelBehaviour#tick()}
     * */
    public boolean skipOriginalTick() {
        return true;
    }

    /**
     * @return Whether the panel should render its bulb
     * */
    public boolean shouldRenderBulb(boolean original) {
        return false;
    }

    /**
     * Since original class extends {@link com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour},
     * return true if you want this gauge to have the render from filtering behavior.
     * */
    public boolean withFilteringBehaviour() {
        return false;
    }

    /**
     * @return The item associated with this behavior. Used for drops and more.
     * */
    public abstract Item getItem();

    /**
     * @return the model for your custom gauge. Will automatically be used for rendering.
     * */
    public abstract PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType);

    /**
     * An easier extension of {@link AbstractPanelBehaviour#write(CompoundTag, boolean)}.
     * @param nbt The compound tag of the single gauge slot. Save your data into this
     * */
    public void easyWrite(CompoundTag nbt, boolean clientPacket) {}

    /**
     * An easier extension of {@link AbstractPanelBehaviour#read(CompoundTag, boolean)}.
     * @param nbt The compound tag of the single gauge slot. Read your data from this slot
     * */
    public void easyRead(CompoundTag nbt, boolean clientPacket) {}

    /**
     * Opens the editor screen for this panel
     * */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new BasicPanelScreen<>(this));
    }

    /**
     * This function is called when trying to connect two gauges together.
     * The default declaration you see below ignores the {@code no_item} issue,
     * since most of the custom gauges do not actually need a custom item inside to connect.
     * @return whether it should ignore or not the issue inserted.
     * */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean ignoreIssue(@Nullable String issue) {
        return "factory_panel.no_item".equals(issue);
    }

    /**
     * @return The path color
     * */
    public int calculatePath(FactoryPanelBehaviour other, int original) {
        return DISABLED;
    }

    public int calculateExtraPath(BlockPos pos) {
        return DISABLED;
    }

    @ApiStatus.Internal
    public <T> Optional<T> getConnectionValue(RegistryObject<PanelConnection<T>> connection) {
        return getConnectionValue(connection.get());
    }

    @ApiStatus.Internal
    public <T> Optional<T> getConnectionValue(PanelConnection<T> connection) {
        if(!connections.containsKey(connection)) return Optional.empty();
        // We can safely cast here.
        //noinspection unchecked
        return Optional.ofNullable((T) connections.get(connection).get());
    }

    public static class PanelConnectionBuilder {
        private final Map<PanelConnection<?>, Supplier<?>> map = new Reference2ObjectArrayMap<>();

        private PanelConnectionBuilder() {}

        public <T> PanelConnectionBuilder put(@NotNull RegistryObject<PanelConnection<T>> panelConnection, @NotNull Supplier<T> getter) {
            return put(panelConnection.get(), getter);
        }

        public <T> PanelConnectionBuilder put(@NotNull PanelConnection<T> panelConnection, @NotNull Supplier<T> getter) {
            map.put(panelConnection, getter);
            return this;
        }
    }

    public PanelType<?> getPanelType() {
        return type;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
        if(blockEntity instanceof FactoryPanelBlockEntity be) {
            var newBehaviour = new FactoryPanelBehaviour(be, this.slot); //TODO: Might break with other mods
            newBehaviour.active = false;
            blockEntity.attachBehaviourLate(newBehaviour);
            be.panels.put(slot, newBehaviour);
            be.redraw = true;
            be.lastShape = null;
            be.notifyUpdate();
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        CompoundTag panelTag = nbt.getCompound(CreateLang.asId(slot.name()));
        if (panelTag.isEmpty()) {
            active = false;
            return;
        } else {
            active = true;
        }
        easyRead(panelTag, clientPacket);
    }

    public void consumeForLinks(Consumer<FactoryPanelSupportBehaviour> consumer) {
        for(FactoryPanelConnection connection : targetedByLinks.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
            if(linkAt == null) return;
            if(!linkAt.isOutput()) continue;
            consumer.accept(linkAt);
        }
    }


    public <T> void consumeForPanels(PanelConnection<T> panelConnection, Consumer<T> consumer) {
        for(FactoryPanelConnection connection : targetedBy.values()) {
            if(!getWorld().isLoaded(connection.from.pos())) return;
            FactoryPanelBehaviour at = at(getWorld(), connection);
            if(at == null) return;
            var opt = EGPanelConnections.getConnectionValue(at, panelConnection);
            if(opt.isEmpty()) continue;
            consumer.accept(opt.get());
        }
    }

    public <T> void consumeForExtra(PanelConnection<T> panelConnection, BiConsumer<BlockPos, T> consumer) {
        Set<BlockPos> toRemove = new HashSet<>();
        for(var connection : ((IFPExtra)this).extra_gauges$getExtra().values()) {
            var pos = connection.from.pos();
            if(!getWorld().isLoaded(pos)) return;
            var level = getWorld();
            var state = level.getBlockState(pos);
            var be = level.getBlockEntity(pos);
            var listener = panelConnection.getListener(state.getBlock());
            if(listener == null) {
                toRemove.add(connection.from.pos());
                continue;
            }
            var opt = listener.invalidate(level, state, pos, be);
            opt.ifPresent(t -> consumer.accept(pos, t));

        }
        toRemove.forEach(pos -> ((IFPExtra)this).extra_gauges$getExtra().remove(pos));
        if(!toRemove.isEmpty()) blockEntity.notifyUpdate();
    }

    //use later
    public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        super.writeSafe(nbt);
        CompoundTag special = nbt.contains("CustomPanels") ? nbt.getCompound("CustomPanels") : new CompoundTag();
        special.putString(CreateLang.asId(slot.name()), Objects.requireNonNull(EGRegistries.PANEL_REGISTRY.get().getKey(type)).toString());
        nbt.put("CustomPanels", special);
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        CompoundTag special = nbt.contains("CustomPanels") ? nbt.getCompound("CustomPanels") : new CompoundTag();
        special.putString(CreateLang.asId(slot.name()), Objects.requireNonNull(EGRegistries.PANEL_REGISTRY.get().getKey(type)).toString());
        nbt.put("CustomPanels", special);
        super.write(nbt, clientPacket);
        //We avoid adding some data that is pointless in a generic gauge.
        // You can re-add it in your custom write method though
        //NOTE: If you feel like some data should not be avoided, please open a GitHub issue to report this.
        // This API is still very WIP and support is very well accepted
        if (!active)
            return;

        CompoundTag panelTag = new CompoundTag();
        panelTag.putBoolean("Satisfied", satisfied);
        panelTag.putBoolean("PromisedSatisfied", promisedSatisfied);
        panelTag.putBoolean("RedstonePowered", redstonePowered);
        panelTag.put("Targeting", NBTHelper.writeCompoundList(this.targeting, FactoryPanelPosition::write));
        panelTag.put("TargetedBy", NBTHelper.writeCompoundList(this.targetedBy.values(), FactoryPanelConnection::write));
        panelTag.put("TargetedByLinks", NBTHelper.writeCompoundList(this.targetedByLinks.values(), FactoryPanelConnection::write));
        IFPExtra extra = ((IFPExtra)this);
        panelTag.put("TargetedByExtra", NBTHelper.writeCompoundList(extra.extra_gauges$getExtra().values(), FactoryPanelConnection::write));
        if(extra.extra_gauges$getWidth() != 3) panelTag.putInt("extra_gauges$CraftWidth", extra.extra_gauges$getWidth());

        if(withFilteringBehaviour()) {
            panelTag.put("Filter", getFilter().serializeNBT());
            panelTag.putInt("FilterAmount", count);
            panelTag.putBoolean("UpTo", upTo);
        }

        easyWrite(panelTag, clientPacket);

        nbt.put(CreateLang.asId(slot.name()), panelTag);
    }

    @Override
    public boolean canShortInteract(ItemStack toApply) {
        return withFilteringBehaviour() && super.canShortInteract(toApply);
    }

    @Override
    public ItemStack getFilter() {
        return getConnectionValue(EGPanelConnections.FILTER).orElse(FilterItemStack.empty()).item();
    }

    // We invoke the private function through mixin. Create, why are you making this method private...
    public void notifyRedstoneOutputs() {
        for(FactoryPanelPosition panelPos : targeting) {
            if(!getWorld().isLoaded(panelPos.pos()))
                return;
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(getWorld(), panelPos);
            if(behaviour == null) continue;
            behaviour.checkForRedstoneInput();
        }
        ((FactoryPanelBehaviourIMixin)this).extra_gauges$notifyRedstoneOutputs();
    }

    @Override
    public boolean acceptsValueSettings() {
        return true;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getItem().getDefaultInstance().getHoverName();
    }

    @Override
    public ItemRequirement getRequiredItems() {
        return isActive() ? new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, getItem())
                : ItemRequirement.NONE;
    }
}
