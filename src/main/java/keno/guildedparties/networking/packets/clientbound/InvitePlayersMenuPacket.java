package keno.guildedparties.networking.packets.clientbound;

import java.util.List;

public record InvitePlayersMenuPacket(List<String> usernames) {
}
