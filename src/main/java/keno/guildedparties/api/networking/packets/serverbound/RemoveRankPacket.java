package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;

public record RemoveRankPacket(String guildName, Rank rank) {
    public static StructEndec<RemoveRankPacket> endec = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_name", RemoveRankPacket::guildName),
            Rank.ENDEC.fieldOf("rank", RemoveRankPacket::rank),
            RemoveRankPacket::new);
}
