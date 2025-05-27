package net.liukrast.eg.api.logistics.board;

import net.liukrast.eg.api.EGRegistries;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * */
public class PanelConnection<T> {
    private final ColorProvider<T> provider;
    private BlockCapability<T, PanelContext> cap;
    private final Class<T> clazz;

    public PanelConnection(ColorProvider<T> provider, Class<T> clazz) {
        this.provider = provider;
        this.clazz = clazz;
    }
    /**
     * UNSAFE
     * */
    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public int getColorGeneric(Supplier<?> from, Supplier<?> to) {
        return provider.provide((T) from.get(), (T) to.get());
    }

    public interface ColorProvider<T> {
        int provide(T from, T to);
    }

    public BlockCapability<T, PanelContext> asCapability() {
        if(cap == null) {
            var id = Objects.requireNonNull(EGRegistries.PANEL_CONNECTION_REGISTRY.getKey(this));
            cap = BlockCapability.create(
                    ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_out"),
                    clazz,
                    PanelContext.class
            );
        }
        return cap;
    }

    public record PanelContext(AttachFace attachFace, Direction direction) {
        public static PanelContext from(BlockState blockState) {
            if(!blockState.hasProperty(FaceAttachedHorizontalDirectionalBlock.FACING) || !blockState.hasProperty(FaceAttachedHorizontalDirectionalBlock.FACE)) return null;
            return new PanelContext(blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE), blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACING));
        }

        public boolean matches(BlockState blockState) {
            return blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE).equals(attachFace) && blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACING).equals(direction);
        }
    }
}
