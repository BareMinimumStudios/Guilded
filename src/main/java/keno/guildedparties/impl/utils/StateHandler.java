package keno.guildedparties.impl.utils;

import keno.guildedparties.impl.server.StateSaverAndLoader;

@FunctionalInterface
public interface StateHandler {
    void handleState(StateSaverAndLoader state);
}
