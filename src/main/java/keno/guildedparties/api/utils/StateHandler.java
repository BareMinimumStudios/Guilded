package keno.guildedparties.api.utils;

import keno.guildedparties.api.server.StateSaverAndLoader;

@FunctionalInterface
public interface StateHandler {
    void handleState(StateSaverAndLoader state);
}
