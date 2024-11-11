package keno.guildedparties.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildBanList;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"UnstableApiUsage"})
public class GuildManagementCommands {
    public static int createGuildCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (player == null) return 0;

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            String guildName = context.getArgument("guildName", String.class);
            if (!state.guilds.containsKey(guildName)) {
                String leaderRankName = context.getArgument("leaderRankName", String.class);
                Rank leaderRank = new Rank(leaderRankName, 1);
                Pair<UUID, Rank> leader = new Pair<>(player.getUuid(), leaderRank);
                Guild guild = new Guild(guildName, List.of(leader), List.of(leaderRank));
                GuildSettings settings = GuildSettings.getDefaultSettings();
                GuildBanList list = new GuildBanList(new ArrayList<>());
                state.guilds.put(guildName, guild);
                state.banLists.put(guildName, list);
                state.guildSettingsMap.put(guildName, settings);
                player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(guildName, leaderRank));
                server.getPlayerManager().broadcast(Text.of("New guild has been created by "
                        + player.getName().getLiteralString() + ": " + guildName), false);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("This guild already exists"), true);
            }
        } else {
            player.sendMessageToClient(Text.of("You are already in a guild"), true);
        }
        return 0;
    }

    public static int disbandGuildCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity leader = source.getPlayer();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (leader == null) return 0;

        if (leader.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            Member member = leader.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            if (member.rank().isCoLeader()) {
                Guild guild = state.guilds.get(member.guildKey());
                for (UUID id : guild.getPlayers().keySet()) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                    if (player != null) {
                        guild.removePlayerFromGuild(player);
                    }
                }
                state.guildSettingsMap.remove(member.guildKey());
                state.guilds.remove(member.guildKey());
                state.banLists.remove(member.guildKey());
                server.getPlayerManager().broadcast(Text.of("The guild, " + guild.getName() + ", has been disbanded"), false);
                return 1;
            } else {
                leader.sendMessageToClient(Text.of("You must be the Leader to disband the guild"), true);
            }
        } else {
            leader.sendMessageToClient(Text.of("You aren't in a guild"), true);
        }
        return 0;
    }

    public static int removeGuildRankCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (sender == null) return 0;

        if (!sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            sender.sendMessageToClient(Text.of("You aren't in a guild"), true);
            return 0;
        }

        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        GuildSettings settings = state.guildSettingsMap.get(senderData.guildKey());
        if (senderData.rank().priority() <= settings.manageGuildPriority()) {
            String rankName = StringArgumentType.getString(context, "rank");
            if (rankName.equals("Recruit")) {
                sender.sendMessageToClient(Text.of("The Recruit rank can't be removed"), true);
                return 0;
            }
            Rank rank = state.guilds.get(senderData.guildKey()).getRank(rankName);
            for (UUID id : state.guilds.get(senderData.guildKey()).getPlayers().keySet()) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                if (player != null) {
                    if (player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).rank().equals(rank)) {
                        state.guilds.get(senderData.guildKey()).demoteMember(player);
                    }
                }
            }
            return state.guilds.get(senderData.guildKey()).removeRank(rankName);
        } else {
            sender.sendMessageToClient(Text.of("Your rank is too low priority"), true);
        }
        return 0;
    }

    public static int createGuildRankCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (sender == null) return 0;

        if (!sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            sender.sendMessageToClient(Text.of("You aren't in a guild"), true);
            return 0;
        }
        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        GuildSettings settings = state.guildSettingsMap.get(senderData.guildKey());
        if (senderData.rank().priority() <= settings.manageGuildPriority()) {
            String rankName = StringArgumentType.getString(context, "rankName");
            int rankPriority = IntegerArgumentType.getInteger(context, "rankPriority");
            if (rankPriority > senderData.rank().priority()) {
                Rank rank = new Rank(rankName, rankPriority);
                int status = state.guilds.get(senderData.guildKey()).addRank(rank);
                if (status == 1) {
                    sender.sendMessageToClient(Text.of("Rank added successfully!"), true);
                } else if (status == 0) {
                    sender.sendMessageToClient(Text.of("Can only add ranks lowers than yours"), true);
                }
                return status;
            }
        } else {
            sender.sendMessageToClient(Text.of("Your rank is too low priority"), true);
        }
        return 0;
    }
}
