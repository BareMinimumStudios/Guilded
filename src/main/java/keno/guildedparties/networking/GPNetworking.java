package keno.guildedparties.networking;


import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.packets.DoesPlayerHaveGuildPacket;
import keno.guildedparties.networking.packets.GetOwnGuildPacket;
import keno.guildedparties.networking.packets.GuildedMenuPacket;
import keno.guildedparties.networking.packets.OwnGuildMenuPacket;
import keno.guildedparties.utils.GuildUtils;
import net.minecraft.server.network.ServerPlayerEntity;

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

        GP_CHANNEL.registerServerbound(GetOwnGuildPacket.class, GetOwnGuildPacket.endec.structOf("get_own_guild"), (handler, access) -> {
            ServerPlayerEntity player = access.player();
            Member member = handler.member();

            Guild guild = GuildUtils.getGuild(player).orElseThrow();
            GP_CHANNEL.serverHandle(player).send(OwnGuildMenuPacket.createFromGuild(member, guild));
        });
    }
}
