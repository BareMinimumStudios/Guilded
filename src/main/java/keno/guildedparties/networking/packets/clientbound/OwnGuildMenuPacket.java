package keno.guildedparties.networking.packets.clientbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.client.screens.own_guild.OwnGuildMenu;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;

import java.util.List;
import java.util.Map;

/** Packet used to open the menu of the player's guild
 * @see OwnGuildMenu OwnGuildMenu */
public record OwnGuildMenuPacket(Member member, Map<String, Rank> players, List<Rank> ranks, String summary) {
    public static Endec<OwnGuildMenuPacket> endec = StructEndecBuilder.of(
            Member.endec.fieldOf("clientMember", OwnGuildMenuPacket::member),
            Rank.endec.mapOf().fieldOf("players", OwnGuildMenuPacket::players),
            Rank.endec.listOf().fieldOf("ranks", OwnGuildMenuPacket::ranks),
            Endec.STRING.fieldOf("summary", OwnGuildMenuPacket::summary),
            OwnGuildMenuPacket::new);

    public static OwnGuildMenuPacket createFromGuild(Member member, Guild guild) {
        return new OwnGuildMenuPacket(member, guild.getPlayers(), guild.getRanks(), guild.getDescription());
    }
}
