package keno.guildedparties.api.data.player;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record Invite(String guildName, String inviteSender) {
    public static final StructEndec<Invite> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_name", Invite::guildName),
            StructEndec.STRING.fieldOf("invite_sender", Invite::inviteSender),
            Invite::new);
}
