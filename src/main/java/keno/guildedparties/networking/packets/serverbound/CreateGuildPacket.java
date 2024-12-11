package keno.guildedparties.networking.packets.serverbound;

public record CreateGuildPacket(String guildName, String leaderRankName, String description) {
}
