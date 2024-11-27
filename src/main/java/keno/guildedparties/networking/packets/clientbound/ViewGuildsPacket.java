package keno.guildedparties.networking.packets.clientbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.client.screens.view_guilds.ViewGuildsMenu;

import java.util.List;

public record ViewGuildsPacket(List<ViewGuildsMenu.GuildDisplayInfo> infos, boolean isInGuild) {
    public static Endec<ViewGuildsPacket> endec = StructEndecBuilder.of(
            ViewGuildsMenu.GuildDisplayInfo.endec.listOf().fieldOf("displayInfo", ViewGuildsPacket::infos),
            Endec.BOOLEAN.fieldOf("isInGuild", ViewGuildsPacket::isInGuild),
            ViewGuildsPacket::new);
}
