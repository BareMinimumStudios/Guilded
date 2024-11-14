package keno.guildedparties.networking.packets;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.utils.GPEndecs;

public record GetOwnGuildPacket(Member member) {
    public static Endec<GetOwnGuildPacket> endec = StructEndecBuilder.of(
            GPEndecs.MEMBER.fieldOf("member", GetOwnGuildPacket::member),
            GetOwnGuildPacket::new);
}
