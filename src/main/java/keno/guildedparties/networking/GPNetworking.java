package keno.guildedparties.networking;


import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.packets.*;
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

            if (!player.getGameProfile().getName().equals(handler.guildmateName())) {
                Member senderData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                if (senderData.getRank().priority() <= settings.managePlayerPriority()) {
                    Rank guildmateData = state.getGuild(handler.guildName()).getPlayers().get(handler.guildmateName());
                    if (guildmateData.priority() > senderData.getRank().priority()) {
                        state.getGuild(handler.guildName()).removePlayerFromGuild(server, handler.guildmateName());
                        state.getBanlist(handler.guildName()).banPlayer(handler.guildmateName());
                        state.markDirty();

                        player.sendMessageToClient(Text.translatable("guildedparties.ban_successful"), true);
                        server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_banned",
                                handler.guildmateName(), handler.guildName()), false);
                    } else {
                        player.sendMessageToClient(Text.translatable("guildedparties.player_is_higher_priority"), true);
                    }
                } else {
                    player.sendMessageToClient(Text.translatable("guildedparties.need_higher_priority",
                            String.valueOf(settings.managePlayerPriority())), true);
                }
            } else {
                player.sendMessageToClient(Text.translatable("guildedparties.cant_perform_on_self"), true);
            }
        });

        GP_CHANNEL.registerServerbound(KickGuildmatePacket.class, (handler, access) -> {
            MinecraftServer server = access.runtime();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            ServerPlayerEntity player = access.player();

            GuildSettings settings = state.getSettings(handler.guildName());

            if (!player.getGameProfile().getName().equals(handler.guildmateName())) {
                Member senderData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                if (senderData.getRank().priority() <= settings.managePlayerPriority()) {
                    Rank guildmateData = state.getGuild(handler.guildName()).getPlayers().get(handler.guildmateName());
                    if (guildmateData.priority() > senderData.getRank().priority()) {
                        state.getGuild(handler.guildName()).removePlayerFromGuild(server, handler.guildmateName());
                        state.markDirty();

                        player.sendMessageToClient(Text.translatable("guildedparties.kick_successful"), true);
                        server.getPlayerManager().broadcast(Text.translatable("guildedparties.player_was_kicked",
                                handler.guildmateName(), handler.guildName()), false);
                    } else {
                        player.sendMessageToClient(Text.translatable("guildedparties.player_is_higher_priority"), true);
                    }
                } else {
                    player.sendMessageToClient(Text.translatable("guildedparties.need_higher_priority",
                            String.valueOf(settings.managePlayerPriority())), true);
                }
            } else {
                player.sendMessageToClient(Text.translatable("guildedparties.cant_perform_on_self"), true);
            }
        });
    }
}
