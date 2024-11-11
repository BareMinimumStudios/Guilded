package keno.guildedparties.server.commands.invites;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.player.Invite;
import keno.guildedparties.data.player.Member;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class InvitePlayerCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ServerCommandSource source = commandContext.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(commandContext.getArgument("player", String.class));
        if (sender != null && player != null) {
            if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member member = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    Registry<GuildSettings> settingsRegistry = server.getRegistryManager().getOrThrow(GuildedParties.SETTINGS_REGISTRY);
                    GuildSettings settings = settingsRegistry.getEntry(GuildedParties.GPLoc(member.guildKey())).orElseThrow().value();
                    if (member.rank().priority() <= settings.invitePlayersPriority()) {
                        if (!player.hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
                            Invite invite = new Invite(member.guildKey(), sender.getUuid());
                            player.setAttached(GPAttachmentTypes.INVITE_ATTACHMENT, invite);
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
        }

        return 0;
    }
}
