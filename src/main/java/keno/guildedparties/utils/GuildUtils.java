package keno.guildedparties.utils;

import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
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

    public static void broadcastToGuildmates(MinecraftServer server, String message, ServerPlayerEntity sender) {
        if (sender == null) return;

        if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            Member member = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            Guild guild = state.guilds.get(member.guildKey());
            String rankName = "[%s][%s]: ".formatted(sender.getGameProfile().getName(), member.rank().name());
            message = rankName + message;
            broadcastToGuildmates(server, guild, message);
        }
    }

    /** A method that can retrieve a optional containing a guild for you,
     * avoiding having to repeatedly write boilerplate for guild evaluation and assessment
     * @param guildName The name of the guild you're retrieving, sometimes called a "guildKey" internally
     * @return Optional that will contain a guild object, or be empty if the guild isn't found
     * @see Member
     * @see Guild
     * */
    public static Optional<Guild> getGuild(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (!state.guilds.containsKey(guildName)) return Optional.empty();

        return Optional.of(state.guilds.get(guildName));
    }

    /** Overload of GuildUtils#getGuild that uses a ServerPlayerEntity instead for simplicity
     * @param player The player to retrieve a guild object from, via their Member data
     * @see Member
     * @see Guild
     * @see GuildUtils#getGuild(MinecraftServer, String)
     * */
    public static Optional<Guild> getGuild(ServerPlayerEntity player) {
        if (player != null || !player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return Optional.empty();

        Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        MinecraftServer server = player.getServer();
        return getGuild(server, member.guildKey());
    }
}
