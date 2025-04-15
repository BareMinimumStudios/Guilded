package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;

public record ModifyRankPacket(String guildName, Rank oldRank, Rank newRank) {
    public static StructEndec<ModifyRankPacket> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guildName", ModifyRankPacket::guildName),
            Rank.ENDEC.fieldOf("oldRank", ModifyRankPacket::oldRank),
            Rank.ENDEC.fieldOf("newRank", ModifyRankPacket::newRank),
            ModifyRankPacket::new);
}
