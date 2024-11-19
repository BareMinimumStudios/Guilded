package keno.guildedparties.networking;


import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.packets.clientbound.GuildedMenuPacket;
import keno.guildedparties.networking.packets.clientbound.OwnGuildMenuPacket;
import keno.guildedparties.networking.packets.serverbound.*;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.utils.GuildUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** We will be using OwoLib's networking API due to the limitation and complexity of FAPI's networking */
@SuppressWarnings("UnstableApiUsage")
public class GPNetworking {
    public static final OwoNetChannel GP_CHANNEL = OwoNetChannel.create(GuildedParties.GPLoc("gp_channel"));

    public static void init() {
        GP_CHANNEL.registerClientboundDeferred(GuildedMenuPacket.class, GuildedMenuPacket.endec.structOf("open_guilded_menu_packet"));
        GP_CHANNEL.registerClientboundDeferred(OwnGuildMenuPacket.class, OwnGuildMenuPacket.endec.structOf("own_guild_menu_packet"));

        GP_CHANNEL.registerServerbound(DoesPlayerHaveGuildPacket.class, (handler, access) -> {
            ServerPlayerEntity player = access.player();

            boolean isInGuild = player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            GP_CHANNEL.serverHandle(player).send(new GuildedMenuPacket(isInGuild));
        });

        GP_CHANNEL.registerServerbound(GetOwnGuildPacket.class, (handler, access) -> {
            ServerPlayerEntity player = access.player();
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);

            Guild guild = GuildUtils.getGuild(player).orElseThrow();
            GP_CHANNEL.serverHandle(player).send(OwnGuildMenuPacket.createFromGuild(member, guild));
        });

        GP_CHANNEL.registerServerbound(BanGuildmatePacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            ServerPlayerEntity player = access.player();

            GuildSettings settings = state.getSettings(handler.guildName());

            if (!areSenderAndPlayerSame(player, handler.guildmateName())) {
                if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                    if (isSenderHigherPriorityThanPlayer(state, player, handler.guildmateName())) {
                        state.getGuild(handler.guildName()).removePlayerFromGuild(server, handler.guildmateName());
                        state.getBanlist(handler.guildName()).banPlayer(handler.guildmateName());
                        state.markDirty();

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
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            ServerPlayerEntity player = access.player();

            GuildSettings settings = state.getSettings(handler.guildName());

            if (!areSenderAndPlayerSame(player, handler.guildmateName())) {
                if (canSenderPerformAction(player, settings.managePlayerPriority())) {
                    if (isSenderHigherPriorityThanPlayer(state, player, handler.guildmateName())) {
                        state.getGuild(handler.guildName()).removePlayerFromGuild(server, handler.guildmateName());
                        state.markDirty();

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

        GP_CHANNEL.registerServerbound(ChangePlayerRankPacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            ServerPlayerEntity sender = access.player();

            GuildSettings settings = state.getSettings(handler.guildName());

            if (!areSenderAndPlayerSame(sender, handler.username())) {
                if (canSenderPerformAction(sender, settings.managePlayerRankPriority())) {
                    if (isSenderHigherPriorityThanPlayer(state, sender, handler.username())) {
                        state.getGuild(handler.guildName()).changeMemberRank(server, handler.username(), handler.rank());
                        state.markDirty();

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
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            ServerPlayerEntity sender = access.player();

            if (!sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).getRank().isCoLeader()) {
                state.getGuild(handler.guildName()).removePlayerFromGuild(sender);
                state.markDirty();
                sender.sendMessageToClient(Text.translatable("guildedparties.leaving_successful"), true);
                server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_left_guild",
                        sender.getGameProfile().getName(), handler.guildName()), false);
            } else {
                sender.sendMessageToClient(Text.translatable("guildedparties.must_stand_down"), true);
            }
        });
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

    public static boolean isSenderHigherPriorityThanPlayer(StateSaverAndLoader state, ServerPlayerEntity sender,
                                                 String playerName) {
        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        Rank playerData = state.getGuild(senderData.getGuildKey()).getRank(playerName);
        boolean areThey = playerData.priority() > senderData.getRank().priority();

        if (!areThey) {
            sender.sendMessageToClient(Text.translatable("guildedparties.player_is_higher_priority"), true);
        }

        return areThey;
    }
}
