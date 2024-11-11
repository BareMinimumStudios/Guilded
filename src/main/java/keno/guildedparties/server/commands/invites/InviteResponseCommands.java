package keno.guildedparties.server.commands.invites;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Invite;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class InviteResponseCommands {
    public static int declineInviteCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            if (player.hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
                player.removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                player.sendMessageToClient(Text.of("Invite has been declined successfully"), true);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("There's no invite to decline"), true);
            }
        }
        return 0;
    }

    public static int acceptInviteCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();
        if (player != null) {
            if (player.hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
                if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Invite invite = player.getAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                    StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                    if (state.guilds.containsKey(invite.guildName())) {
                        if (state.guilds.get(invite.guildName()).getPlayers().containsKey(invite.inviteSender())) {
                            state.guilds.get(invite.guildName()).addPlayerToGuild(player, "Recruit");
                            return 1;
                        } else {
                            player.sendMessageToClient(Text.of("The invite sender is no longer in this guild"), true);
                        }
                    } else {
                        player.sendMessageToClient(Text.of("The guild who sent this invite no longer exists"), true);
                    }
                    player.removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                } else {
                    player.sendMessageToClient(Text.of("Already in a guild, leave it before you accept again"), true);
                }
            } else {
                player.sendMessageToClient(Text.of("No invite to accept, or the invite expired"), true);
            }
        }
        return 0;
    }
}
