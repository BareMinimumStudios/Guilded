package keno.guildedparties.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record ChangePlayerRankPacket(Rank rank, String guildName, String username) {
    public static StructEndec<ChangePlayerRankPacket> endec = StructEndecBuilder.of(
            Rank.endec.fieldOf("rank", ChangePlayerRankPacket::rank),
            Endec.STRING.fieldOf("guildName", ChangePlayerRankPacket::guildName),
            Endec.STRING.fieldOf("username", ChangePlayerRankPacket::username),
            ChangePlayerRankPacket::new);
}
