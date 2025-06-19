package keno.guildedparties.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** We use this plugin to handle mixins depending on if other mods are present
 * @see GPMixinConfigPlugin#shouldApplyMixin(String, String)
 * **/
public class GPMixinConfigPlugin implements IMixinConfigPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger(GPMixinConfigPlugin.class);

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
        if (areMixinsTheSame(mixinClassName, qualifyServerMixinName("ServerPlayNetworkHandlerMixin"))) {
            boolean styledChatPresent = isModPresent("styledchat");
            if (styledChatPresent) LOGGER.info("Styled-Chat detected! Disabling the guilded note-system in favor of SC");
            return !styledChatPresent;
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

    private boolean areMixinsTheSame(String mixinClassName, String otherMixinClassName) {
        return mixinClassName.equals(otherMixinClassName);
    }

    /** Removes the need to type the fully qualified class name
     * @param mixinClassName the class name to qualify
     * @return the qualified mixin class name **/
    private String qualifyDefaultMixinName(String mixinClassName) {
        return "keno.guildedparties.mixin." + mixinClassName;
    }

    /// @see GPMixinConfigPlugin#qualifyDefaultMixinName(String)
    private String qualifyServerMixinName(String mixinClassName) {
        return qualifyDefaultMixinName("server." + mixinClassName);
    }

    /// Used in checking if a mixin should apply
    private boolean isModPresent(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).isPresent();
    }
}
