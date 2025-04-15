package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;

public record ChangePlayerRankPacket(Rank rank, String guildName, String username) {
    public static StructEndec<ChangePlayerRankPacket> ENDEC = StructEndecBuilder.of(
            Rank.ENDEC.fieldOf("rank", ChangePlayerRankPacket::rank),
            StructEndec.STRING.fieldOf("guildName", ChangePlayerRankPacket::guildName),
            StructEndec.STRING.fieldOf("username", ChangePlayerRankPacket::username),
            ChangePlayerRankPacket::new);
}
