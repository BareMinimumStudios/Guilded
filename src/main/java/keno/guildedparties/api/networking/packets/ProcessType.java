package keno.guildedparties.api.networking.packets;

import io.wispforest.endec.StructEndec;
import net.minecraft.util.StringIdentifiable;

public enum ProcessType implements StringIdentifiable {
    GUILD("guild"),
    SHOP("shop"),
    CHAT("chat"),
    NONE("none");

    private final String type;

    ProcessType(String type) {
        this.type = type;
    }

    @Override
    public String asString() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
