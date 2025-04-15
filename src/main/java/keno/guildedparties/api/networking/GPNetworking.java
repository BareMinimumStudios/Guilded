package keno.guildedparties.api.networking;

import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.client.screens.view_guilds.ViewGuildsMenu;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.Rank;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.networking.packets.ProcessType;
import keno.guildedparties.api.networking.packets.clientbound.*;
import keno.guildedparties.api.networking.packets.serverbound.NoParamServerPacket;
import keno.guildedparties.api.networking.packets.serverbound.StrParamServerPacket;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GPNetworking {
    public static final OwoNetChannel GP_CHANNEL = OwoNetChannel.create(GuildedParties.modLoc("gp_channel"));
    public static final OwoNetChannel CHAT_CHANNEL = OwoNetChannel.createOptional(GuildedParties.modLoc("gp_chat"));
    public static final OwoNetChannel SHOP_CHANNEL = OwoNetChannel.createOptional(GuildedParties.modLoc("gp_shop"));

    public static void init() {
        GP_CHANNEL.registerClientboundDeferred(GuildedMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(OwnGuildMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(InvitePlayersMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(GuildSettingsMenuPacket.class);

        GP_CHANNEL.registerClientboundDeferred(ViewGuildsPacket.class);

        GP_CHANNEL.registerClientboundDeferred(KickedFromMenuPacket.class);

        GP_CHANNEL.registerServerbound(NoParamServerPacket.class, (packet, access) -> {
            ProcessType type = ProcessType.valueOf(packet.type());
            int flag = packet.flag();
            MinecraftServer server = access.runtime();
            ServerPlayerEntity sender = access.player();
            boolean senderIsInGuild = doesPlayerHaveMemberData(sender);

            switch (type) {
                case GUILD: {
                    switch (flag) {
                        case 0: {
                            List<ViewGuildsMenu.GuildDisplayInfo> infos = new ArrayList<>();
                            GuildApi.forEachGuildInServer(server, guild -> {
                                String guildName = guild.getName();

                                String leaderName = guild.getPlayers().keySet().stream().filter(username
                                        -> guild.getPlayerRank(username).isCoLeader()).findFirst().orElseThrow();

                                int members = guild.getPlayers().size();

                                String description = guild.getDescription();

                                infos.add(new ViewGuildsMenu.GuildDisplayInfo(guildName, leaderName,
                                        members, description));
                            });

                            GP_CHANNEL.serverHandle(sender).send(new ViewGuildsPacket(infos, senderIsInGuild));
                        }
                        case 1: GP_CHANNEL.serverHandle(sender).send(new GuildedMenuPacket(senderIsInGuild));
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
                        }
                        case 3: {
                            if (senderIsInGuild) {
                                Member member = GPComponents.MEMBER_KEY.get(sender).getMemberData();

                                Guild guild = GuildApi.getGuild(sender).orElseThrow();
                                GP_CHANNEL.serverHandle(sender).send(OwnGuildMenuPacket.createFromGuild(access.runtime(), member, guild));
                            } else {
                                sender.sendMessageToClient(Text.translatable("guildedparties.not_in_guild"), true);
                            }
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
                        }
                    }
                }
                case SHOP, CHAT, default: GuildedParties.LOGGER.warn("Process type {} should not be used at the moment", type.asString());
            }
        });

        GP_CHANNEL.registerServerbound(StrParamServerPacket.class, (packet, access) -> {

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
}
