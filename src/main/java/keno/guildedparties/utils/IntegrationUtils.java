package keno.guildedparties.utils;

import keno.guildedparties.GuildedParties;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IntegrationUtils {
    /**
     * Uses reflection to safely handle soft integrations that lack entrypoints
     * @param modId The id of the mod to check for
     * @param compatClassName The class to handle the compatibility through; must implement GPIntegration
     * @return True if the integration is enabled, false if it isn't or if an error occurs
     * @see keno.guildedparties.integration.GPIntegration GPIntegration
     */
    public static boolean safeInitializeCompat(String modId, String compatClassName) {
        try {
            if (FabricLoader.getInstance().getModContainer(modId).isPresent()) {
                Class<?> clazz = Class.forName(compatClassName);
                Method integrate =  clazz.getMethod("integrate");
                integrate.invoke(null);
                return true;
            } else {
                return false;
            }
        } catch (ClassNotFoundException | NoSuchMethodException
            | InvocationTargetException | IllegalAccessException e) {
            GuildedParties.LOGGER.warn("Could not find compatibility, despite mod being 'supported': \nMod: {}, compat class: {}",
                    modId, compatClassName);
        }
        return false;
    }
}
