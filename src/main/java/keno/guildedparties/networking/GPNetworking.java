package keno.guildedparties.networking;


import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ViewGuildsMenu;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildBanList;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Invite;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.packets.clientbound.*;
import keno.guildedparties.networking.packets.serverbound.*;
import keno.guildedparties.utils.GuildApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/** We will be using OwoLib's networking API due to the limitation and complexity of FAPI's networking */
@SuppressWarnings({"UnstableApiUsage", "DataFlowIssue"})
public class GPNetworking {
    public static final OwoNetChannel GP_CHANNEL = OwoNetChannel.create(GuildedParties.GPLoc("gp_channel"));
    // This is currently unused, but will be once guild shops are added
    public static final OwoNetChannel GP_SHOPS_CHANNEL = OwoNetChannel.create(GuildedParties.GPLoc("gp_shops_channel"));

    public static void init() {
        GP_CHANNEL.registerClientboundDeferred(GuildedMenuPacket.class, GuildedMenuPacket.endec.structOf("open_guilded_menu_packet"));

        GP_CHANNEL.registerClientboundDeferred(OwnGuildMenuPacket.class, OwnGuildMenuPacket.endec.structOf("own_guild_menu_packet"));

        GP_CHANNEL.registerClientboundDeferred(InvitePlayersMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(GuildSettingsMenuPacket.class,
                GuildSettingsMenuPacket.endec.structOf("guild_settings_menu"));

        GP_CHANNEL.registerClientboundDeferred(ViewGuildsPacket.class,
                ViewGuildsPacket.endec.structOf("view_guilds"));


        GP_CHANNEL.registerServerbound(DoesPlayerHaveGuildPacket.class, (handler, access) -> {
            ServerPlayerEntity player = access.player();

            boolean isInGuild = player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            GP_CHANNEL.serverHandle(player).send(new GuildedMenuPacket(isInGuild));
        });

        GP_CHANNEL.registerServerbound(GetOwnGuildPacket.class, (handler, access) -> {
            ServerPlayerEntity player = access.player();
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            Guild guild = GuildApi.getGuild(player).orElseThrow();
            GP_CHANNEL.serverHandle(player).send(OwnGuildMenuPacket.createFromGuild(member, guild));
        });

        GP_CHANNEL.registerServerbound(BanGuildmatePacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            GuildSettings settings = GuildApi.getSettings(server, handler.guildName());

            if (!areSenderAndPlayerSame(player, handler.guildmateName())) {
                if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                    if (isSenderHigherPriorityThanPlayer(player, handler.guildmateName())) {
                        GuildApi.modifyGuildPersistentState(server, state -> {
                            state.getGuild(handler.guildName()).removePlayerFromGuild(server, handler.guildmateName());
                            state.getBanlist(handler.guildName()).banPlayer(handler.guildmateName());
                        });

                        player.sendMessageToClient(Text.translatable("guildedparties.ban_successful"), true);
                        server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_banned",
                                handler.guildmateName(), handler.guildName()), false);
                    }
                } else {
                    player.sendMessageToClient(Text.translatable("guildedparties.need_higher_priority",
                            String.valueOf(settings.managePlayerPriority())), true);
                }
            }
        });

        GP_CHANNEL.registerServerbound(KickGuildmatePacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            GuildSettings settings = GuildApi.getSettings(server, handler.guildName());

            if (!areSenderAndPlayerSame(player, handler.guildmateName())) {
                if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                    if (isSenderHigherPriorityThanPlayer(player, handler.guildmateName())) {
                        GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(handler.guildName())
                                .removePlayerFromGuild(server, handler.guildmateName()));

                        player.sendMessageToClient(Text.translatable("guildedparties.kick_successful"), true);
                        server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_kicked",
                                handler.guildmateName(), handler.guildName()), false);
                    }
                } else {
                    player.sendMessageToClient(Text.translatable("guildedparties.need_higher_priority",
                            String.valueOf(settings.managePlayerPriority())), true);
                }
            }
        });

        GP_CHANNEL.registerServerbound(ChangePlayerRankPacket.class, ChangePlayerRankPacket.endec.structOf("change_player_rank"), (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, handler.guildName());

            if (!areSenderAndPlayerSame(sender, handler.username())) {
                if (canSenderPerformAction(sender, settings.managePlayerRankPriority())) {
                    if (isSenderHigherPriorityThanPlayer(sender, handler.username())) {
                        GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(handler.guildName()).changeMemberRank(server,
                                handler.username(), handler.rank()));

                        sender.sendMessageToClient(Text.translatable("guildedparties.rank_change_successful"), true);

                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(handler.username());
                        if (player != null) {
                            player.sendMessageToClient(Text.translatable("guildedparties.rank_was_changed",
                                    handler.rank().name()), false);
                        }
                    }
                }
            }
        });

        GP_CHANNEL.registerServerbound(LeaveGuildPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            if (!isSenderLeader(sender)) {
                GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(handler.guildName()).removePlayerFromGuild(sender));
                sender.sendMessageToClient(Text.translatable("guildedparties.leaving_successful"), true);
                server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_left_guild",
                        sender.getGameProfile().getName(), handler.guildName()), false);
            } else {
                sender.sendMessageToClient(Text.translatable("guildedparties.must_stand_down"), true);
            }
        });

        GP_CHANNEL.registerServerbound(GetInvitablePlayersPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            GuildSettings settings = GuildApi.getSettings(server, senderData.getGuildKey());
            if (canSenderPerformAction(sender, settings.invitePlayersPriority())) {
                List<String> invitablePlayersUsernames = new ArrayList<>();

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                        invitablePlayersUsernames.add(player.getGameProfile().getName());
                    }
                }

                GP_CHANNEL.serverHandle(sender).send(new InvitePlayersMenuPacket(invitablePlayersUsernames));
            }
        });

        GP_CHANNEL.registerServerbound(InvitePlayerPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            if (!GuildApi.getGuild(sender).orElseThrow().isPlayerInGuild(handler.username())) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(handler.username());
                if (!player.hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
                    player.setAttached(GPAttachmentTypes.INVITE_ATTACHMENT,
                            new Invite(senderData.getGuildKey(), sender.getGameProfile().getName()));

                    sender.sendMessageToClient(Text.translatable("guildedparties.invite_successful"), true);
                    player.sendMessageToClient(Text.translatable("guildedparties.invite_received",
                            player.getGameProfile().getName(), senderData.getGuildKey()), false);
                } else {
                    sender.sendMessageToClient(Text.translatable("guildedparties.has_invite_already"), true);
                }
            }
        });

        GP_CHANNEL.registerServerbound(ChangeGuildSettingsPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            if (isSenderLeader(sender)) {
                GuildApi.modifyGuildPersistentState(server, state -> state.addSettings(handler.settings(), handler.guildName()));
            }
        });

        GP_CHANNEL.registerServerbound(GetGuildSettingsPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, handler.guildName());

            GP_CHANNEL.serverHandle(sender).send(new GuildSettingsMenuPacket(handler.guildName(), settings));
        });

        GP_CHANNEL.registerServerbound(AddRankPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            Rank rank = new Rank(handler.rankName(), handler.rankPriority());

            GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(handler.guildName()).addRank(rank));

            sender.sendMessageToClient(Text.translatable("guildedparties.rank_added",
                    handler.rankName()), false);
        });

        GP_CHANNEL.registerServerbound(RemoveRankPacket.class, RemoveRankPacket.endec.structOf("remove_rank_packet"), (handler, access)
                -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            Guild guild = GuildApi.getGuild(server, handler.guildName()).orElseThrow();

            GuildApi.modifyGuildPersistentState(server, state -> {
                state.getGuild(handler.guildName()).removeRank(handler.rank().name());

                server.getPlayerManager().getPlayerList().forEach(player -> {
                    if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                        if (player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).getGuildKey().equals(handler.guildName())) {
                            Member guildmateData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                            if (guildmateData.getRank().equals(handler.rank())) {
                                state.getGuild(handler.guildName()).demoteMember(player);
                            }
                        }
                    }
                });

                for (String username : state.getGuild(handler.guildName()).getPlayers().keySet()) {
                    if (state.getGuild(handler.guildName()).getPlayerRank(username).equals(handler.rank())) {
                        state.getGuild(handler.guildName()).demoteMember(server, username);
                    }
                }
            });

            sender.sendMessageToClient(Text.translatable("guildedparties.rank_removal_successful"),
                    true);

            GuildApi.broadcastToGuildmates(server, guild, Text.translatable("guildedparties.rank_was_removed",
                    handler.rank().name()));
        });

        GP_CHANNEL.registerServerbound(ModifyRankPacket.class, ModifyRankPacket.endec.structOf("modify_rank_packet"), (handler, access) -> {
            MinecraftServer server = access.runtime();

            GuildApi.modifyGuildPersistentState(server, state -> {
                Set<String> usernames = state.getGuild(handler.guildName()).getPlayers().keySet();
                for (String username : usernames) {
                    if (state.getGuild(handler.guildName()).getPlayerRank(username).equals(handler.oldRank())) {
                        state.getGuild(handler.guildName()).changeMemberRank(server, username, handler.newRank());
                    }
                }
                state.getGuild(handler.guildName()).removeRank(handler.oldRank().name());
                state.getGuild(handler.guildName()).addRank(handler.newRank());
            });

            access.player().sendMessageToClient(Text.translatable("guildedparties.rank_modified"), true);
        });

        GP_CHANNEL.registerServerbound(StepDownPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            String senderUsername = sender.getGameProfile().getName();

            if (isSenderLeader(sender)) {
                if (!areSenderAndPlayerSame(sender, handler.username())) {
                    Rank leaderRank = GuildApi.getGuild(sender).orElseThrow().getPlayerRank(sender);
                    GuildApi.modifyGuildPersistentState(server, (state -> {
                        state.getGuild(handler.guildName()).demoteMember(server, senderUsername);
                        state.getGuild(handler.guildName()).changeMemberRank(server, handler.username(), leaderRank);
                    }));

                    sender.sendMessageToClient(Text.translatable("guildedparties.stepped_down"), true);
                    server.getPlayerManager().broadcast(Text.translatable("guildedparties.leader_stepped_down",
                            senderUsername, handler.guildName(), handler.username()), false);
                    ServerPlayerEntity newLeader = server.getPlayerManager().getPlayer(handler.username());
                    if (newLeader != null) {
                        newLeader.sendMessageToClient(Text.translatable("guildedparties.new_leader", handler.guildName()), true);
                    }
                }
            }
        });

        GP_CHANNEL.registerServerbound(DisbandGuildPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();

            GuildApi.modifyGuildPersistentState(server, state -> {
                for (String username : state.getGuild(handler.guildName()).getPlayers().keySet()) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(username);
                    if (player != null) {
                        player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                    }
                }
                state.removeGuild(handler.guildName());
            });

            server.getPlayerManager().broadcast(Text.translatable("guildedparties.guild_disbanded",
                    handler.guildName()), false);
        });

        GP_CHANNEL.registerServerbound(CreateGuildPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            String guildName = handler.guildName();
            Rank leadershipRank = new Rank(handler.leaderRankName(), 1);

            if (GuildApi.getGuild(server, guildName).isEmpty()) {
                String username = player.getGameProfile().getName();
                Map<String, Rank> playerMap = Map.of(username, leadershipRank);
                List<Rank> ranks = List.of(leadershipRank);
                Guild guild = new Guild(guildName, playerMap, ranks, "none");

                GuildApi.modifyGuildPersistentState(server, state -> {
                    state.addGuild(guild);
                    state.addSettings(GuildSettings.getDefaultSettings(), guildName);
                    state.addBanlist(new GuildBanList(List.of()), guildName);
                });

                player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(guildName, leadershipRank));

                server.getPlayerManager().broadcast(Text.translatable("guildedparties.guild_was_created",
                        handler.guildName(), username), false);
            } else {
                player.sendMessageToClient(Text.translatable("guildedparties.guild_exists",
                        handler.guildName()), true);
            }
        });

        GP_CHANNEL.registerServerbound(GetGuildInfosPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();
            List<ViewGuildsMenu.GuildDisplayInfo> displayInfos = new ArrayList<>();
            boolean playerIsInGuild = player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            GuildApi.forEachGuildInServer(server, guild -> {
                String guildName = guild.getName();

                String leaderName = guild.getPlayers().keySet().stream().filter(username
                        -> guild.getPlayerRank(username).isCoLeader()).findFirst().orElseThrow();

                int members = guild.getPlayers().size();

                displayInfos.add(new ViewGuildsMenu.GuildDisplayInfo(guildName, leaderName, members));
            });



            GP_CHANNEL.serverHandle(player).send(new ViewGuildsPacket(displayInfos, playerIsInGuild));
        });
    }

    public static boolean isSenderLeader(ServerPlayerEntity player) {
        return player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).getRank().isCoLeader();
    }

    public static boolean canSenderPerformAction(ServerPlayerEntity sender, int priorityNeeded) {
        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        boolean canThey = senderData.getRank().priority() <= priorityNeeded;

        if (!canThey) {
            sender.sendMessageToClient(Text.translatable("guildedparties.need_higher_priority",
                    String.valueOf(priorityNeeded)), true);
        }

        return canThey;
    }

    public static boolean areSenderAndPlayerSame(ServerPlayerEntity sender, String playerUsername) {
        boolean areThey = sender.getGameProfile().getName().equals(playerUsername);

        if (areThey) {
            sender.sendMessageToClient(Text.translatable("guildedparties.cant_perform_on_self"), true);
        }

        return sender.getGameProfile().getName().equals(playerUsername);
    }

    public static boolean isSenderHigherPriorityThanPlayer(ServerPlayerEntity sender,
                                                 String playerName) {
        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        Rank playerData = GuildApi.getGuild(sender).orElseThrow().getRank(playerName);
        boolean areThey = playerData.priority() > senderData.getRank().priority();

        if (!areThey) {
            sender.sendMessageToClient(Text.translatable("guildedparties.player_is_higher_priority"), true);
        }

        return areThey;
    }
}
