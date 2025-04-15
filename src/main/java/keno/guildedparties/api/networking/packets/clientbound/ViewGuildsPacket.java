package keno.guildedparties.api.networking.packets.clientbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.client.screens.view_guilds.ViewGuildsMenu;

import java.util.List;

public record ViewGuildsPacket(List<ViewGuildsMenu.GuildDisplayInfo> infos, boolean isInGuild) {
    public static StructEndec<ViewGuildsPacket> endec = StructEndecBuilder.of(
            ViewGuildsMenu.GuildDisplayInfo.ENDEC.listOf().fieldOf("displayInfo", ViewGuildsPacket::infos),
            StructEndec.BOOLEAN.fieldOf("isInGuild", ViewGuildsPacket::isInGuild),
            ViewGuildsPacket::new);

}
