package keno.guildedparties.api.networking.packets.clientbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;

public record OwnGuildMenuPacket(Member member, Map<String, Rank> players,
                                 List<Rank> ranks, String summary, boolean hasCustomTextures) {
    public static final StructEndec<OwnGuildMenuPacket> ENDEC = StructEndecBuilder.of(
            Member.ENDEC.fieldOf("clientMember", OwnGuildMenuPacket::member),
            Rank.ENDEC.mapOf().fieldOf("players", OwnGuildMenuPacket::players),
            Rank.ENDEC.listOf().fieldOf("ranks", OwnGuildMenuPacket::ranks),
            StructEndec.STRING.fieldOf("summary", OwnGuildMenuPacket::summary),
            StructEndec.BOOLEAN.fieldOf("has_textures", OwnGuildMenuPacket::hasCustomTextures),
            OwnGuildMenuPacket::new);

    public static OwnGuildMenuPacket createFromGuild(MinecraftServer server, Member member, Guild guild) {
        GuildSettings settings = GuildApi.getSettings(server, member.getGuildKey());
        return new OwnGuildMenuPacket(member, guild.getPlayers(), guild.getRanks(), guild.getDescription(), settings.hasCustomTextures());
    }
}
