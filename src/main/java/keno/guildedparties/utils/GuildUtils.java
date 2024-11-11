package keno.guildedparties.utils;

import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

/** Utility functions for guilds */
@SuppressWarnings("UnstableApiUsage")
public class GuildUtils {
    /** A static method to send a message to all players in a guild
     * @see GuildUtils GuildUtils for overloads
     * */
    public static void broadcastToGuildmates(MinecraftServer server, Guild guild, Text text) {
        Text message = Text.of("[GC] ").copy().append(text).withColor(0xffffcc00);
        for (UUID memberId : guild.getPlayers().keySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(memberId);
            if (player != null) {
                player.sendMessageToClient(message, false);
            }
        }
    }

    public static void broadcastToGuildmates(MinecraftServer server, Guild guild, String message) {
        broadcastToGuildmates(server, guild, Text.of(message));
    }

    public static void broadcastToGuildmates(MinecraftServer server, Guild guild, String message, ServerPlayerEntity sender) {
        if (sender == null) return;

        if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            Member member = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            String rankName = "<%s>[%s]: ".formatted(sender.getGameProfile().getName(), member.rank().name());
            message = rankName + message;
            broadcastToGuildmates(server, guild, message);
        }
    }
}
