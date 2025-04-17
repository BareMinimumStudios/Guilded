package keno.guildedparties.api.networking.packets;

import keno.guildedparties.api.data.guilds.Guild;
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

    /** Kludge **/
    public static ProcessType stringToType(String str) {
        if (str.equals("guild")) return GUILD;
        else if (str.equals("chat")) return CHAT;
        else if (str.equals("shop")) return SHOP;
        return NONE;
    }
}
