package keno.guildedparties.server.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class GuildmateManagementCommands {
    public static int standDownCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity oldLeader = source.getPlayer();
        ServerPlayerEntity newLeader = server.getPlayerManager().getPlayer(context.getArgument("newLeader", String.class));
        if (oldLeader != null) {
            if (newLeader != null) {
                if (oldLeader.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member oldLeaderData = oldLeader.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    if (newLeader.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)
                            && newLeader.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).guildKey().equals(oldLeaderData.guildKey())) {
                        if (oldLeaderData.rank().isCoLeader()) {
                            Text message = Text.of(oldLeader.getName().getLiteralString()
                                    + " has resigned leadership of " + oldLeaderData.guildKey() + " to " + newLeader.getName().getLiteralString());
                            server.getPlayerManager().broadcast(Text.of(message), false);

                            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                            state.guilds.get(oldLeaderData.guildKey()).demoteMember(oldLeader);
                            return state.guilds.get(oldLeaderData.guildKey()).changeMemberRank(newLeader, oldLeaderData.rank());
                        } else {
                            oldLeader.sendMessageToClient(Text.of("You aren't the leader of this guild"), true);
                        }
                    } else {
                        oldLeader.sendMessageToClient(Text.of("This player isn't in your guild"), true);
                    }
                } else {
                    oldLeader.sendMessageToClient(Text.of("You aren't in a guild"), true);
                }
            } else {
                oldLeader.sendMessageToClient(Text.of("This player doesn't exist"), true);
            }
        }
        return 0;
    }

    public static int banPlayerCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));

        if (sender != null && player != null) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    if (playerData.guildKey().equals(senderData.guildKey())) {
                        Rank playerRank = playerData.rank();
                        Rank senderRank = senderData.rank();

                        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                        GuildSettings settings = state.guildSettingsMap.get(playerData.guildKey());
                        if (playerRank.priority() > senderRank.priority() && senderRank.priority() <= settings.managePlayerPriority()) {
                            state.guilds.get(playerData.guildKey()).removePlayerFromGuild(player);
                            state.banLists.get(playerData.guildKey()).banPlayer(player.getUuid());
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static int kickPlayerCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));

        if (sender != null && player != null) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    if (playerData.guildKey().equals(senderData.guildKey())) {
                        Rank playerRank = playerData.rank();
                        Rank senderRank = senderData.rank();

                        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                        GuildSettings settings = state.guildSettingsMap.get(playerData.guildKey());
                        if (playerRank.priority() > senderRank.priority() && senderRank.priority() <= settings.managePlayerPriority()) {
                            state.guilds.get(playerData.guildKey()).removePlayerFromGuild(player);
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static int demotePlayerCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));

        if (player != null && sender != null) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    if (playerData.guildKey().equals(senderData.guildKey())) {
                        Rank playerRank = playerData.rank();
                        Rank senderRank = senderData.rank();

                        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                        GuildSettings settings = state.guildSettingsMap.get(playerData.guildKey());
                        if (playerRank.priority() > senderRank.priority() && senderRank.priority() <= settings.managePlayerRankPriority()) {
                            int status = state.guilds.get(playerData.guildKey()).demoteMember(player);
                            if (status == 0) {
                                sender.sendMessageToClient(Text.of("Could not demote player"), true);
                            } else if (status == 1) {
                                sender.sendMessageToClient(Text.of("Demotion successful!"), true);
                                if (server.isDedicated()) {
                                    player.sendMessageToClient(Text.of("You have been demoted"), true);
                                }
                            }
                            return status;
                        } else {
                            sender.sendMessageToClient(Text.of("You aren't high enough rank to demote this person"), true);
                        }
                    } else {
                        sender.sendMessageToClient(Text.of("You and this player aren't in the same guild"), true);
                    }
                } else {
                    sender.sendMessageToClient(Text.of("You aren't in a guild"), true);
                }
            } else {
                sender.sendMessageToClient(Text.of("This player isn't in a guild"), true);
            }
        }
        return 0;
    }

    public static int promotePlayerCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));

        if (player != null && sender != null) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    if (playerData.guildKey().equals(senderData.guildKey())) {
                        Rank playerRank = playerData.rank();
                        Rank senderRank = senderData.rank();

                        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                        GuildSettings settings = state.guildSettingsMap.get(playerData.guildKey());
                        if (playerRank.priority() > senderRank.priority() && senderRank.priority() <= settings.managePlayerRankPriority()) {
                            int status = state.guilds.get(playerData.guildKey()).promoteMember(player);
                            if (status == 0) {
                                sender.sendMessageToClient(Text.of("Could not demote player"), true);
                            } else if (status == 1) {
                                sender.sendMessageToClient(Text.of("Demotion successful!"), true);
                                if (server.isDedicated()) {
                                    player.sendMessageToClient(Text.of("You have been demoted"), true);
                                }
                            }
                            return status;
                        } else {
                            sender.sendMessageToClient(Text.of("You aren't high enough rank to demote this person"), true);
                        }
                    } else {
                        sender.sendMessageToClient(Text.of("You and this player aren't in the same guild"), true);
                    }
                } else {
                    sender.sendMessageToClient(Text.of("You aren't in a guild"), true);
                }
            } else {
                sender.sendMessageToClient(Text.of("This player isn't in a guild"), true);
            }
        }
        return 0;
    }
}
