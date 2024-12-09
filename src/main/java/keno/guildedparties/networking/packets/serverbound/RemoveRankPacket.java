package keno.guildedparties.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record RemoveRankPacket(String guildName, Rank rank) {
    public static StructEndec<RemoveRankPacket> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guild_name", RemoveRankPacket::guildName),
            Rank.endec.fieldOf("rank", RemoveRankPacket::rank),
            RemoveRankPacket::new);
}
