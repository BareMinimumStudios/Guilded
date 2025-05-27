package keno.guildedparties.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record ModifyRankPacket(String guildName, Rank oldRank, Rank newRank) {
    public static StructEndec<ModifyRankPacket> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guildName", ModifyRankPacket::guildName),
            Rank.ENDEC.fieldOf("oldRank", ModifyRankPacket::oldRank),
            Rank.ENDEC.fieldOf("newRank", ModifyRankPacket::newRank),
            ModifyRankPacket::new);
}
