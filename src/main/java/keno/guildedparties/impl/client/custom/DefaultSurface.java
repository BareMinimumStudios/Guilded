package keno.guildedparties.impl.client.custom;

import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.DyeColor;

public enum DefaultSurface {
    BOXES(Surface.flat(Color.ofDye(DyeColor.GRAY).rgb()).and(Surface.outline(0))),
    MINECRAFT(Surface.PANEL);

    private final Surface surface;

    DefaultSurface(Surface surface) {
        this.surface = surface;
    }

    public Surface getSurface() {
        return surface;
    }
}
