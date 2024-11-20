package keno.guildedparties.utils;

import keno.guildedparties.server.StateSaverAndLoader;

@FunctionalInterface
public interface StateHandler {
    void handleState(StateSaverAndLoader state);
}
