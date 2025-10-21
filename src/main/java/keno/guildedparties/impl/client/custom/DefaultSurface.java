package keno.guildedparties.impl.client.custom;

import io.wispforest.owo.ui.core.Surface;
import keno.guildedparties.GuildedParties;

public enum DefaultSurface {
    CLASSIC(Surface.PANEL),
    // Most unexpectedly good UI-style I have ever found
    AZURE(Surface.flat(0x808080FF).and(Surface.outline(0x000000FF))),
    SYNTH_WAVE(GPSurfaces.createCustomSurface(GuildedParties.GPLoc("synth_wave")));

    private final Surface surface;

    DefaultSurface(Surface surface) {
        this.surface = surface;
    }

    public Surface getSurface() {
        return surface;
    }
}
