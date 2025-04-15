package keno.guildedparties.api.networking.packets.serverbound;

public record KickGuildmatePacket(String guildName, String guildmateName) {
}
