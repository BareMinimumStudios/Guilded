package keno.guildedparties.client.custom;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.NinePatchTexture;
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
}
