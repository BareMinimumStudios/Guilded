package keno.guildedparties.impl.client.custom.surface;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.NinePatchTexture;
import keno.guildedparties.GuildedParties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GPSurfaces {
    public static Surface createCustomSurface(Identifier surface) {
        return (context, component) -> {
            drawPanel(surface, context, component.x(), component.y(), component.width(), component.height());
        };
    }

    private static void drawPanel(Identifier surface, OwoUIDrawContext context, int x, int y, int width, int height) {
        NinePatchTexture.draw(surface, context, x, y, width, height);
    }

    /**
     * Gets the custom UI surface/appearance for a guild; returns the default if a location error occurs
     * @param guildName the name of the guild
     * @param flag determines which type of surface is grabbed
     * @return the surface to apply
     */
    public static Surface getGuildSurface(String guildName, int flag, boolean alwaysUseMain) {
        StringBuilder builder = new StringBuilder(guildName.strip().toLowerCase()
                .replace(" ", "_").replaceAll("[^a-zA-Z0-9]", ""));

        if (alwaysUseMain) {
            builder.append("_main");
            return createCustomSurface(GuildedParties.GPLoc(builder.toString()));
        }

        boolean useDefaultSurface = false;
        switch (flag) {
            // Main surface
            case 0: {
                builder.append("_main");
                break;
            }
            // Used for the guild's entry in guild view
            case 1: {
                builder.append("_entry");
                break;
            }
            default: {
                useDefaultSurface = true;
                break;
            }
        }

        if (useDefaultSurface) return GuildedParties.CONFIG.defaultUIStyle().getSurface();

        Identifier guildSurfaceId = GuildedParties.GPLoc(builder.toString());
        return createCustomSurface(guildSurfaceId);
    }
}
