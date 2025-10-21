package keno.guildedparties.impl.client.custom;

import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Surface;
import keno.guildedparties.GuildedParties;

//TODO fix the Boxed default-style
public enum DefaultSurface {
    CLASSIC(Surface.PANEL),
    BOXED(Surface.outline(0x77000000)),
    SYNTH_WAVE(GPSurfaces.createCustomSurface(GuildedParties.GPLoc("synth_wave")));

    private final Surface surface;

    DefaultSurface(Surface surface) {
        this.surface = surface;
    }

    public Surface getSurface() {
        return surface;
    }
}
