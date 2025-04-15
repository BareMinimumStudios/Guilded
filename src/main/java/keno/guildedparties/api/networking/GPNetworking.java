package keno.guildedparties.api.networking;

import io.wispforest.owo.network.OwoNetChannel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.networking.packets.clientbound.*;

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


    }
}
