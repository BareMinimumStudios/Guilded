package keno.guildedparties.impl.mixin.server;

public interface PlayerTicksImpl {
    int guildedparties$getTick(int flag);

    void guildedparties$decrementTick(int flag);
}
