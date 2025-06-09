package keno.guildedparties.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/// We use this plugin to check whether or not a mod is detected, and decide what mixins should be used depending on that
public class GPMixinConfigPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        /* If Styled Chat is detected, we disable the built-in message note system in favor of it */
        if (mixinClassName.equals("keno.guildedparties.mixin.server.ServerPlayNetworkHandlerMixin")) {
            return !isModPresent("styledchat");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    /// Used in checking if a mixin should apply
    private boolean isModPresent(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).isPresent();
    }
}
