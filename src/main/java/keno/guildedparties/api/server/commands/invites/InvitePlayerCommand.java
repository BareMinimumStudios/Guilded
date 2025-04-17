package keno.guildedparties.api.server.commands.invites;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.player.Invite;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.networking.GPNetworking;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InvitePlayerCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ServerCommandSource source = commandContext.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(commandContext.getArgument("player", String.class));
        if (sender != null && player != null) {
            if (!sender.equals(player)) {
                if (!GPNetworking.doesPlayerHaveMemberData(player)) {
                    if (GPNetworking.doesPlayerHaveMemberData(sender)) {
                        Member member = GPComponents.MEMBER_KEY.get(sender).getMemberData();
                        GuildSettings settings = GuildApi.getSettings(server, member.getGuildKey());
                        if (member.getRank().priority() <= settings.invitePlayersPriority()) {
                            if (!GPNetworking.doesPlayerHaveInviteData(player)) {
                                Invite invite = new Invite(member.getGuildKey(), sender.getGameProfile().getName());
                                GPComponents.INVITE_KEY.get(player).setInvite(invite);
                                sender.sendMessageToClient(Text.of("Invite sent successfully"), true);
                                if (server.isDedicated()) {
                                    player.sendMessageToClient(Text.of("Invite received, will expire in 90 seconds"), false);
                                }
                                return 1;
                            } else {
                                sender.sendMessageToClient(Text.of("This player already has an invite, try again later"), true);
                            }
                        } else {
                            sender.sendMessageToClient(Text.of("You aren't high enough priority to invite people"), true);
                        }
                    } else {
                        sender.sendMessageToClient(Text.of("You must be in a guild to invite this person"), true);
                    }
                } else {
                    sender.sendMessageToClient(Text.of("This player is already in a guild"), true);
                }
            } else {
                sender.sendMessageToClient(Text.of("You can't invite yourself..."), true);
            }
        }

        return 0;
    }
}
