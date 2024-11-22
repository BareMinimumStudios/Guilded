package keno.guildedparties.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record ModifyRankPacket(String guildName, Rank oldRank, Rank newRank) {
    public static Endec<ModifyRankPacket> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guildName", ModifyRankPacket::guildName),
            Rank.endec.fieldOf("oldRank", ModifyRankPacket::oldRank),
            Rank.endec.fieldOf("newRank", ModifyRankPacket::newRank),
            ModifyRankPacket::new);
}
