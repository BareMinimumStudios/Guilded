package keno.guildedparties.networking.packets.serverbound;

public record KickGuildmatePacket(String guildName, String guildmateName) {}
