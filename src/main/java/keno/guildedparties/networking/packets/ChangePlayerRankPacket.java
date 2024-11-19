package keno.guildedparties.networking.packets;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record ChangePlayerRankPacket(Rank rank, String guildName, String username) {
    public static Endec<ChangePlayerRankPacket> endec = StructEndecBuilder.of(
            Rank.endec.fieldOf("rank", ChangePlayerRankPacket::rank),
            Endec.STRING.fieldOf("guildName", ChangePlayerRankPacket::guildName),
            Endec.STRING.fieldOf("username", ChangePlayerRankPacket::username),
            ChangePlayerRankPacket::new);
}
