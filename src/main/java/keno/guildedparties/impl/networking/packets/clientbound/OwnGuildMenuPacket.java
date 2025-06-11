package keno.guildedparties.impl.networking.packets.clientbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.impl.client.screens.own_guild.OwnGuildMenu;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.GuildSettings;
import keno.guildedparties.impl.data.guilds.Rank;
import keno.guildedparties.impl.data.player.Member;
import keno.guildedparties.api.GuildApi;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;

/** Packet used to open the menu of the player's guild
 * @see OwnGuildMenu OwnGuildMenu */
public record OwnGuildMenuPacket(Member member, Map<String, Rank> players,
                                 List<Rank> ranks, String summary, boolean hasCustomTextures) {
    public static StructEndec<OwnGuildMenuPacket> endec = StructEndecBuilder.of(
            Member.ENDEC.fieldOf("clientMember", OwnGuildMenuPacket::member),
            Rank.ENDEC.mapOf().fieldOf("players", OwnGuildMenuPacket::players),
            Rank.ENDEC.listOf().fieldOf("ranks", OwnGuildMenuPacket::ranks),
            Endec.STRING.fieldOf("summary", OwnGuildMenuPacket::summary),
            Endec.BOOLEAN.fieldOf("has_textures", OwnGuildMenuPacket::hasCustomTextures),
            OwnGuildMenuPacket::new);

    public static OwnGuildMenuPacket createFromGuild(MinecraftServer server, Member member, Guild guild) {
        GuildSettings settings = GuildApi.getSettings(server, member.getGuildKey());
        return new OwnGuildMenuPacket(member, guild.getPlayers(), guild.getRanks(), guild.getDescription(), settings.hasCustomTextures());
    }
}
