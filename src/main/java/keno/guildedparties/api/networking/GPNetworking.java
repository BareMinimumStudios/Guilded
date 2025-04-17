package keno.guildedparties.api.networking;

import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.Rank;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildBanList;
import keno.guildedparties.api.data.guilds.GuildDisplayInfo;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.player.Invite;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.networking.packets.ProcessType;
import keno.guildedparties.api.networking.packets.clientbound.*;
import keno.guildedparties.api.networking.packets.serverbound.*;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GPNetworking {
    public static final OwoNetChannel GP_CHANNEL = OwoNetChannel.create(GuildedParties.modLoc("gp_channel"));
    public static final OwoNetChannel CHAT_CHANNEL = OwoNetChannel.createOptional(GuildedParties.modLoc("gp_chat"));
    public static final OwoNetChannel SHOP_CHANNEL = OwoNetChannel.createOptional(GuildedParties.modLoc("gp_shop"));

    public static void init() {
        // Register object serializers
        PacketBufSerializer.register(Member.class, Member.SERIALIZER);

        PacketBufSerializer.register(Rank.class, Rank.SERIALIZER);

        PacketBufSerializer.register(GuildSettings.class, GuildSettings.SERIALIZER);

        PacketBufSerializer.register(GuildDisplayInfo.class, GuildDisplayInfo.SERIALIZER);

        PacketBufSerializer.register(OwnGuildMenuPacket.class, OwnGuildMenuPacket.SERIALIZER);

        PacketBufSerializer.register(GuildSettingsMenuPacket.class, GuildSettingsMenuPacket.SERIALIZER);

        PacketBufSerializer.register(ViewGuildsPacket.class, ViewGuildsPacket.SERIALIZER);

        // Register client-bound packets safely
        GP_CHANNEL.registerClientboundDeferred(GuildedMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(OwnGuildMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(InvitePlayersMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(GuildSettingsMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(ViewGuildsPacket.class);

        GP_CHANNEL.registerClientboundDeferred(KickedFromMenuPacket.class);

        // Register packets with no parameters
        GP_CHANNEL.registerServerbound(NoParamServerPacket.class, (packet, access) -> {
            ProcessType type = ProcessType.stringToType(packet.type());
            int flag = packet.flag();
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            boolean senderIsInGuild = doesPlayerHaveMemberData(sender);

            switch (type) {
                case GUILD: {
                    switch (flag) {
                        case 0: {
                            List<GuildDisplayInfo> infos = new ArrayList<>();
                            GuildApi.forEachGuildInServer(server, guild -> {
                                String guildName = guild.getName();

                                String leaderName = guild.getPlayers().keySet().stream().filter(username
                                        -> guild.getPlayerRank(username).isCoLeader()).findFirst().orElseThrow();

                                int members = guild.getPlayers().size();

                                String description = guild.getDescription();

                                infos.add(new GuildDisplayInfo(guildName, leaderName,
                                        members, description));
                            });

                            GP_CHANNEL.serverHandle(sender).send(new ViewGuildsPacket(infos, senderIsInGuild));
                            break;
                        }
                        case 1: {
                            GP_CHANNEL.serverHandle(sender).send(new GuildedMenuPacket(senderIsInGuild));
                            break;
                        }
                        case 2: {
                            if (!senderIsInGuild) return;

                            Member senderData = GPComponents.MEMBER_KEY.get(sender).getMemberData();

                            GuildSettings settings = GuildApi.getSettings(server, senderData.getGuildKey());
                            if (canSenderPerformAction(sender, settings.invitePlayersPriority())) {
                                List<String> invitablePlayersUsernames = new ArrayList<>();

                                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                    if (!doesPlayerHaveMemberData(player)) {
                                        invitablePlayersUsernames.add(player.getGameProfile().getName());
                                    }
                                }

                                if (!invitablePlayersUsernames.isEmpty()) {
                                    GP_CHANNEL.serverHandle(sender).send(new InvitePlayersMenuPacket(invitablePlayersUsernames));
                                } else {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.no_invitable_players"), false);
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (senderIsInGuild) {
                                Member member = GPComponents.MEMBER_KEY.get(sender).getMemberData();

                                Guild guild = GuildApi.getGuild(sender).orElseThrow();
                                GP_CHANNEL.serverHandle(sender).send(OwnGuildMenuPacket.createFromGuild(access.runtime(), member, guild));
                            } else {
                                sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
                            }
                            break;
                        }
                        case 4: {
                            var obj = ConfigSynchronizer.getClientOptions(sender, "gp-config")
                                    .get(GuildedParties.CONFIG.keys.guildToQuickJoin);

                            if (obj instanceof String guildName) {
                                if (senderIsInGuild) {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.already_in_guild"), false);
                                    return;
                                }

                                if (!GuildApi.doesGuildExist(sender, guildName)) {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.guild_doesnt_exist"), false);
                                    return;
                                }

                                if (GuildApi.getSettings(server, guildName).isPrivate()) {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.guild_is_private",
                                            guildName), false);
                                    return;
                                }

                                GuildApi.addPlayerToGuild(sender, guildName);
                                GP_CHANNEL.serverHandle(sender).send(new KickedFromMenuPacket());
                            }

                            break;
                        }
                    }
                }
                case SHOP, CHAT, NONE: GuildedParties.LOGGER.warn("Process type {} should not be used at the moment", type.asString());
            }
        });

        GP_CHANNEL.registerServerbound(StrParamServerPacket.class, (packet, access) -> {
            ProcessType type = ProcessType.stringToType(packet.type());
            int flag = packet.flag();
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            boolean senderIsInGuild = doesPlayerHaveMemberData(sender);
            String str = packet.str();

            switch (type) {
                case GUILD:
                    switch (flag) {
                        case 0: {
                            if (senderIsInGuild) {
                                if (isSenderLeader(sender)) {
                                    GuildApi.modifyGuildPersistentState(server, state -> {
                                        for (String username : state.getGuild(str).getPlayers().keySet()) {
                                            ServerPlayerEntity member = server.getPlayerManager().getPlayer(username);
                                            if (member != null) {
                                                GPComponents.MEMBER_KEY.get(sender).changeMemberData(null);
                                            }
                                        }
                                        state.removeGuild(str);
                                    });

                                    server.getPlayerManager().broadcast(Text.translatable("guildedparties.guild_disbanded",
                                            str), false);
                                } else {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.is_not_leader"), true);
                                }
                            }
                            break;
                        }
                        case 1: {
                            GuildSettings settings = GuildApi.getSettings(server, str);

                            GP_CHANNEL.serverHandle(sender).send(new GuildSettingsMenuPacket(str, settings));
                            break;
                        }
                        case 2: {
                            if (senderIsInGuild) {
                                Member senderData = GPComponents.MEMBER_KEY.get(sender).getMemberData();

                                GuildSettings settings = GuildApi.getSettings(server, senderData.getGuildKey());

                                if (canSenderPerformAction(sender, settings.invitePlayersPriority())) {
                                    if (!GuildApi.getBanList(server, senderData.getGuildKey()).isPlayerBanned(str)) {
                                        if (!GuildApi.getGuild(sender).orElseThrow().isPlayerInGuild(str)) {
                                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(str);
                                            if (!doesPlayerHaveInviteData(player)) {
                                                GPComponents.INVITE_KEY.get(player).setInvite(new Invite(senderData.getGuildKey(), sender.getGameProfile().getName()));

                                                sender.sendMessageToClient(Text.translatable("guildedparties.invite_successful"), true);
                                                player.sendMessageToClient(Text.translatable("guildedparties.invite_received",
                                                        player.getGameProfile().getName(), senderData.getGuildKey()), false);
                                            } else {
                                                sender.sendMessageToClient(Text.translatable("guildedparties.has_invite_already"), true);
                                            }
                                        }
                                    } else {
                                        sender.sendMessageToClient(Text.translatable("guildedparties.player_is_banned"), true);
                                    }
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (!GuildApi.getSettings(server, str).isPrivate()) {
                                if (!GuildApi.getBanList(server, str).isPlayerBanned(sender.getGameProfile().getName())) {
                                    GuildApi.modifyGuildPersistentState(server, state
                                            -> state.getGuild(str).addPlayerToGuild(sender, "Recruit"));

                                    GPComponents.MEMBER_KEY.get(sender).changeMemberData(new Member(str, new Rank("Recruit", 50)));

                                    Member member = GPComponents.MEMBER_KEY.get(sender).getMemberData();

                                    GP_CHANNEL.serverHandle(server).send(OwnGuildMenuPacket.createFromGuild(access.runtime(),
                                            member,
                                            GuildApi.getGuild(server, str).orElseThrow()));
                                } else {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.banned"), true);
                                }
                            } else {
                                sender.sendMessageToClient(Text.translatable("guildedparties.guild_is_private",
                                        str), true);
                            }
                            break;
                        }
                        case 4: {
                            if (senderIsInGuild) {
                                Member member = GPComponents.MEMBER_KEY.get(sender).getMemberData();
                                if (member.getGuildKey().equals(str)) {
                                    if (!isSenderLeader(sender)) {
                                        GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(str).removePlayerFromGuild(sender));

                                        sender.sendMessageToClient(Text.translatable("guildedparties.leaving_successful"), true);
                                        server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_left_guild",
                                                sender.getGameProfile().getName(), str), false);
                                    } else {
                                        sender.sendMessageToClient(Text.translatable("guildedparties.must_stand_down"), true);
                                    }
                                } else {
                                    sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
                                }
                            } else {
                                sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
                            }
                            break;
                        }
                    }
                case SHOP, CHAT, NONE: GuildedParties.LOGGER.warn("Process type {} should not be used at the moment", type.asString());
            }
        });

        GP_CHANNEL.registerServerbound(AddRankPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            Rank rank = packet.rank();

            if (rank.isCoLeader() || rank.name().equals("Recruit")) {
                access.player().sendMessageToClient(Text.translatable("guildedparties.rank_cannot_be_add"), false);
                return;
            }

            if (canSenderPerformAction(sender, settings.manageGuildPriority())) {
                GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(packet.guildName()).addRank(rank));

                sender.sendMessageToClient(Text.translatable("guildedparties.rank_added",
                        rank.name()), false);
            }
        });

        GP_CHANNEL.registerServerbound(BanGuildmatePacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (doesPlayerHaveMemberData(player)) {
                if (!areSenderAndPlayerSame(player, packet.guildmateName())) {
                    if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                        if (isSenderHigherPriorityThanPlayer(player, packet.guildmateName())) {
                            GuildApi.modifyGuildPersistentState(server, state -> {
                                state.getGuild(packet.guildName()).removePlayerFromGuild(server, packet.guildmateName());
                                state.getBanlist(packet.guildName()).banPlayer(packet.guildmateName());
                            });

                            player.sendMessageToClient(Text.translatable("guildedparties.ban_successful"), true);
                            server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_banned",
                                    packet.guildmateName(), packet.guildName()), false);
                        }
                    }
                }
            }
        });

        GP_CHANNEL.registerServerbound(ChangeDescriptionPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (canSenderPerformAction(player, settings.manageGuildPriority()) ||
                    isSenderLeader(player)) {
                GuildApi.modifyGuildPersistentState(server, state
                        -> state.getGuild(packet.guildName()).setDescription(packet.description()));

                player.sendMessageToClient(Text.translatable("guildedparties.guild_description_changed",
                        packet.guildName()), false);
            }
        });

        GP_CHANNEL.registerServerbound(ChangeGuildSettingsPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (doesPlayerHaveMemberData(sender)) {
                if (isSenderLeader(sender) || canSenderPerformAction(sender, settings.manageGuildPriority())) {
                    GuildApi.modifyGuildPersistentState(server, state
                            -> state.addSettings(packet.settings(), packet.guildName()));
                }
            } else {
                sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
            }
        });

        GP_CHANNEL.registerServerbound(ChangePlayerRankPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (doesPlayerHaveMemberData(sender)) {
                if (!areSenderAndPlayerSame(sender, packet.username())) {
                    if (canSenderPerformAction(sender, settings.managePlayerRankPriority())) {
                        if (isSenderHigherPriorityThanPlayer(sender, packet.username())) {
                            GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(packet.guildName()).changeMemberRank(server,
                                    packet.username(), packet.rank()));

                            sender.sendMessageToClient(Text.translatable("guildedparties.rank_change_successful"), true);

                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(packet.username());
                            if (player != null) {
                                player.sendMessageToClient(Text.translatable("guildedparties.rank_was_changed",
                                        packet.rank().name()), false);
                            }
                        }
                    }
                }
            } else {
                sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
            }
        });

        GP_CHANNEL.registerServerbound(CreateGuildPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            String guildName = packet.guildName();
            Rank leadershipRank = new Rank(packet.leaderRankName(), 1);

            if (GuildApi.getGuild(server, guildName).isEmpty()) {
                if (!doesPlayerHaveMemberData(player)) {
                    String username = player.getGameProfile().getName();
                    Map<String, Rank> playerMap = Map.of(username, leadershipRank);
                    List<Rank> ranks = List.of(leadershipRank, new Rank("Recruit", 50));
                    Guild guild = new Guild(guildName, playerMap, ranks, packet.description());

                    GuildApi.modifyGuildPersistentState(server, state -> {
                        state.addGuild(guild);
                        state.addSettings(GuildSettings.getDefaultSettings(), guildName);
                        state.addBanlist(new GuildBanList(List.of()), guildName);
                    });

                    GPComponents.MEMBER_KEY.get(player).changeMemberData(new Member(guildName, leadershipRank));

                    server.getPlayerManager().broadcast(Text.translatable("guildedparties.guild_was_created",
                            packet.guildName(), username), false);
                } else {
                    player.sendMessageToClient(Text.translatable("guildedparties.already_in_guild"), true);
                }
            } else {
                player.sendMessageToClient(Text.translatable("guildedparties.guild_exists",
                        packet.guildName()), true);
            }
        });

        GP_CHANNEL.registerServerbound(KickGuildmatePacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (doesPlayerHaveMemberData(player)) {
                if (!areSenderAndPlayerSame(player, packet.guildmateName())) {
                    if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                        if (isSenderHigherPriorityThanPlayer(player, packet.guildmateName())) {
                            GuildApi.modifyGuildPersistentState(server, state -> state.getGuild(packet.guildName())
                                    .removePlayerFromGuild(server, packet.guildmateName()));

                            player.sendMessageToClient(Text.translatable("guildedparties.kick_successful"), true);
                            server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_kicked",
                                    packet.guildmateName(), packet.guildName()), false);
                        }
                    }
                }
            }
        });

        GP_CHANNEL.registerServerbound(ModifyRankPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity player = access.player();
            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            Rank oldRank = packet.oldRank();
            Rank newRank = packet.newRank();

            if (oldRank.isCoLeader() || oldRank.name().equals("Recruit")
                    || newRank.isCoLeader() || newRank.name().equals("Recruit")) {
                access.player().sendMessageToClient(Text.translatable("guildedparties.cannot_modify", oldRank.name()), false);
            }

            if (canSenderPerformAction(player, settings.manageGuildPriority())) {
                GuildApi.modifyGuildPersistentState(server, state -> {
                    Set<String> usernames = state.getGuild(packet.guildName()).getPlayers().keySet();
                    for (String username : usernames) {
                        if (state.getGuild(packet.guildName()).getPlayerRank(username).equals(oldRank)) {
                            state.getGuild(packet.guildName()).changeMemberRank(server, username, newRank);
                        }
                    }
                    state.getGuild(packet.guildName()).removeRank(oldRank.name());
                    state.getGuild(packet.guildName()).addRank(newRank);
                });

                access.player().sendMessageToClient(Text.translatable("guildedparties.rank_modified"), false);
            }
        });

        GP_CHANNEL.registerServerbound(RemoveRankPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = GuildApi.getSettings(server, packet.guildName());

            if (canSenderPerformAction(sender, settings.manageGuildPriority())) {
                Guild guild = GuildApi.getGuild(server, packet.guildName()).orElseThrow();

                GuildApi.modifyGuildPersistentState(server, state -> {
                    state.getGuild(packet.guildName()).removeRank(packet.rank().name());

                    server.getPlayerManager().getPlayerList().forEach(player -> {
                        if (doesPlayerHaveMemberData(player)) {
                            Member guildmateData = GPComponents.MEMBER_KEY.get(player).getMemberData();
                            if (guildmateData.getGuildKey().equals(packet.guildName())) {
                                if (guildmateData.getRank().equals(packet.rank())) {
                                    state.getGuild(packet.guildName()).demoteMember(server, player.getGameProfile().getName());
                                }
                            }
                        }
                    });

                    for (String username : state.getGuild(packet.guildName()).getPlayers().keySet()) {
                        if (state.getGuild(packet.guildName()).getPlayerRank(username).equals(packet.rank())) {
                            state.getGuild(packet.guildName()).demoteMember(server, username);
                        }
                    }
                });

                sender.sendMessageToClient(Text.translatable("guildedparties.rank_removal_successful"),
                        true);

                GuildApi.broadcastToGuildmates(server, guild, Text.translatable("guildedparties.rank_was_removed",
                        packet.rank().name()));
            }
        });

        GP_CHANNEL.registerServerbound(StepDownPacket.class, (packet, access) -> {
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            String senderUsername = sender.getGameProfile().getName();

            if (isSenderLeader(sender)) {
                if (!areSenderAndPlayerSame(sender, packet.username())) {
                    Rank leaderRank = GuildApi.getGuild(sender).orElseThrow().getPlayerRank(sender);
                    GuildApi.modifyGuildPersistentState(server, (state -> {
                        state.getGuild(packet.guildName()).demoteMember(server, senderUsername);
                        state.getGuild(packet.guildName()).changeMemberRank(server, packet.username(), leaderRank);
                    }));

                    sender.sendMessageToClient(Text.translatable("guildedparties.stepped_down"), true);
                    server.getPlayerManager().broadcast(Text.translatable("guildedparties.leader_stepped_down",
                            senderUsername, packet.guildName(), packet.username()), false);
                    ServerPlayerEntity newLeader = server.getPlayerManager().getPlayer(packet.username());
                    if (newLeader != null) {
                        newLeader.sendMessageToClient(Text.translatable("guildedparties.new_leader", packet. guildName()), true);
                    }
                }
            }
        });
    }

    public static boolean isSenderLeader(ServerPlayerEntity player) {
        return GPComponents.MEMBER_KEY.get(player).hasMemberData() && GPComponents.MEMBER_KEY.get(player).isCoLeader();
    }

    public static boolean canSenderPerformAction(ServerPlayerEntity sender, int priorityNeeded) {
        if (!GPComponents.MEMBER_KEY.get(sender).hasMemberData()) return false;

        Member senderData = GPComponents.MEMBER_KEY.get(sender).getMemberData();
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
        if (!GPComponents.MEMBER_KEY.get(sender).hasMemberData()) return false;

        Member senderData = GPComponents.MEMBER_KEY.get(sender).getMemberData();
        Rank playerData = GuildApi.getGuild(sender).orElseThrow().getPlayerRank(playerName);
        boolean areThey = playerData.priority() > senderData.getRank().priority();

        if (!areThey) {
            sender.sendMessageToClient(Text.translatable("guildedparties.player_is_higher_priority"), true);
        }

        return areThey;
    }

    public static boolean doesPlayerHaveMemberData(ServerPlayerEntity player) {
        return GPComponents.MEMBER_KEY.get(player).hasMemberData();
    }

    public static boolean doesPlayerHaveInviteData(ServerPlayerEntity player) {
        return GPComponents.INVITE_KEY.get(player).hasInvite();
    }
}
