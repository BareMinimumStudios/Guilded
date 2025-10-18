package keno.guildedparties.impl.client.custom.abstract_screen;

import io.wispforest.owo.ui.core.Surface;
import keno.guildedparties.GuildedParties;

public interface DefaultSurfaceRetriever {
    default Surface getDefaultSurface() {
        return GuildedParties.CONFIG.defaultUIStyle().getSurface();
    }
}
