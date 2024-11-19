package keno.guildedparties.networking.packets.serverbound;

public record BanGuildmatePacket(String guildName, String guildmateName) {
}
