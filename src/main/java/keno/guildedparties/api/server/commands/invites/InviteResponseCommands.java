package keno.guildedparties.api.server.commands.invites;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.player.Invite;
import keno.guildedparties.api.networking.GPNetworking;
import keno.guildedparties.api.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InviteResponseCommands {
    public static int declineInviteCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            if (GPNetworking.doesPlayerHaveInviteData(player)) {
                GPComponents.INVITE_KEY.get(player).setInvite(null);
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
            if (GPNetworking.doesPlayerHaveInviteData(player)) {
                if (!GPNetworking.doesPlayerHaveMemberData(player)) {
                    Invite invite = GPComponents.INVITE_KEY.get(player).getInvite();
                    StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                    if (state.hasGuild(invite.guildName())) {
                        if (state.getGuild(invite.guildName()).getPlayers().containsKey(invite.inviteSender())) {
                            state.getGuild(invite.guildName()).addPlayerToGuild(player, "Recruit");
                            state.markDirty();
                            return 1;
                        } else {
                            player.sendMessageToClient(Text.of("The invite sender is no longer in this guild"), true);
                        }
                    } else {
                        player.sendMessageToClient(Text.of("The guild who sent this invite no longer exists"), true);
                    }
                    GPComponents.INVITE_KEY.get(player).setInvite(null);
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
