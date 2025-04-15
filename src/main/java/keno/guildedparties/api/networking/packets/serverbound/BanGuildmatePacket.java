package keno.guildedparties.api.networking.packets.serverbound;

public record BanGuildmatePacket(String guildName, String guildmateName) {
}
