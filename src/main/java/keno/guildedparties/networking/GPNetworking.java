package keno.guildedparties.networking;


import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Invite;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.packets.clientbound.GuildSettingsMenuPacket;
import keno.guildedparties.networking.packets.clientbound.GuildedMenuPacket;
import keno.guildedparties.networking.packets.clientbound.InvitePlayersMenuPacket;
import keno.guildedparties.networking.packets.clientbound.OwnGuildMenuPacket;
import keno.guildedparties.networking.packets.serverbound.*;
import keno.guildedparties.utils.GuildApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** We will be using OwoLib's networking API due to the limitation and complexity of FAPI's networking */
@SuppressWarnings("UnstableApiUsage")
public class GPNetworking {
    public static final OwoNetChannel GP_CHANNEL = OwoNetChannel.create(GuildedParties.GPLoc("gp_channel"));

    public static void init() {
        GP_CHANNEL.registerClientboundDeferred(GuildedMenuPacket.class, GuildedMenuPacket.endec.structOf("open_guilded_menu_packet"));
        GP_CHANNEL.registerClientboundDeferred(OwnGuildMenuPacket.class, OwnGuildMenuPacket.endec.structOf("own_guild_menu_packet"));
        GP_CHANNEL.registerClientboundDeferred(InvitePlayersMenuPacket.class);
        GP_CHANNEL.registerClientboundDeferred(GuildSettingsMenuPacket.class,
                GuildSettingsMenuPacket.endec.structOf("guild_settings_menu"));

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
