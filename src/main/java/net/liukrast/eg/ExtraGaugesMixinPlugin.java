package net.liukrast.eg;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ExtraGaugesMixinPlugin implements IMixinConfigPlugin {
    private static final String WITHOUT_FL = "net.liukrast.eg.mixin.withoutfl.";
    private static final String FL_MOD_ID = "create_factory_logistics";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(!mixinClassName.startsWith(WITHOUT_FL)) return true;
        var mods = LoadingModList.get();
        return mods == null || mods.getModFileById(FL_MOD_ID) == null;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
