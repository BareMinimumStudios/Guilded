package keno.guildedparties.api.networking.packets.clientbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.List;

public record InvitePlayersMenuPacket(List<String> usernames) {
    public static final StructEndec<InvitePlayersMenuPacket> ENDEC = StructEndecBuilder.of(
        StructEndec.STRING.listOf().fieldOf("usernames", InvitePlayersMenuPacket::usernames),
    InvitePlayersMenuPacket::new);
}
