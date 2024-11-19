package keno.guildedparties.networking.packets.clientbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

/**Opens the main menu for Guilded in-game*/
public record GuildedMenuPacket(boolean isInGuild) {
    public static Endec<GuildedMenuPacket> endec = StructEndecBuilder.of(
            Endec.BOOLEAN.fieldOf("isInGuild", GuildedMenuPacket::isInGuild),
            GuildedMenuPacket::new);
}
