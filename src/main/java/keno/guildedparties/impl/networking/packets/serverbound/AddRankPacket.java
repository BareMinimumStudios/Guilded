package keno.guildedparties.impl.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.impl.data.guilds.Rank;

public record AddRankPacket(String guildName, Rank rank) {
    public static StructEndec<AddRankPacket> ENDEC = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guildName", AddRankPacket::guildName),
            Rank.ENDEC.fieldOf("rank", AddRankPacket::rank),
            AddRankPacket::new);
}
