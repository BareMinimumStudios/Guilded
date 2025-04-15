package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;

public record AddRankPacket(String guildName, Rank rank) {
    public static final StructEndec<AddRankPacket> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guildName", AddRankPacket::guildName),
            Rank.ENDEC.fieldOf("rank", AddRankPacket::rank),
            AddRankPacket::new);

}
