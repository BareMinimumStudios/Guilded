package keno.guildedparties.networking.packets.serverbound;

public record AddRankPacket(String guildName, String rankName, int rankPriority) {
}
