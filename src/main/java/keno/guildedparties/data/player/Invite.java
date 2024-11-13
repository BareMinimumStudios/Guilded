package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;
import keno.guildedparties.server.commands.invites.InvitePlayerCommand;
import keno.guildedparties.server.commands.invites.InviteResponseCommands;

import java.util.UUID;

/** Record for invite data, used for inviting players to guilds
 * @param guildName guild that the recipient is being invited to
 * @param inviteSender uuid of invite sender, used for security
 * @see InvitePlayerCommand
 * @see InviteResponseCommands
 * */
public record Invite(String guildName, UUID inviteSender) {
    public static Codec<Invite> codec = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.stable().fieldOf("guildName").forGetter(Invite::guildName),
        Uuids.CODEC.stable().fieldOf("inviteSender").forGetter(Invite::inviteSender)
    ).apply(instance, Invite::new));
}
