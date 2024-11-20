package keno.guildedparties.utils;

import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

/** Utility functions for guilds */
@SuppressWarnings("UnstableApiUsage")
public class GuildApi {
    /** Use this method to modify data on the persistent state, without needing to mark it dirty
     * @param server the server to get the state from
     * @param handler lambda that gives you the state
     * */
    public static void modifyGuildPersistentState(MinecraftServer server, StateHandler handler) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        handler.handleState(state);
        state.markDirty();
    }

    /** A static method to send a message to all players in a guild
     * @see GuildApi GuildUtils for overloads
     * */
    public static void broadcastToGuildmates(MinecraftServer server, Guild guild, Text text) {
        Text message = Text.of("[GC] ").copy().append(text).withColor(0xffffcc00);
        for (String username : guild.getPlayers().keySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(username);
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
            Guild guild = state.getGuild(member.getGuildKey());
            String rankName = "[%s][%s] ".formatted(sender.getGameProfile().getName(), member.getRank().name());
            message = rankName + message;
            broadcastToGuildmates(server, guild, message);
        }
    }

    /** A method that can retrieve an optional containing a guild for you,
     * avoiding having to repeatedly write boilerplate for guild evaluation and assessment
     * @param guildName The name of the guild you're retrieving, sometimes called a "guildKey" internally
     * @return Optional that will contain a guild object, or be empty if the guild isn't found
     * @see Member
     * @see Guild
     * @see GuildApi#modifyGuildPersistentState(MinecraftServer, StateHandler) modifyGuildPersistentState
     * for modifying guild data
     * */
    public static Optional<Guild> getGuild(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (!state.hasGuild(guildName)) return Optional.empty();

        return Optional.of(state.getGuild(guildName));
    }

    /** Overload of GuildUtils#getGuild that uses a ServerPlayerEntity instead for simplicity
     * @param player The player to retrieve a guild object from, via their Member data
     * @see Member
     * @see Guild
     * @see GuildApi#getGuild(MinecraftServer, String)
     * */
    public static Optional<Guild> getGuild(ServerPlayerEntity player) {
        if (player == null || !player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return Optional.empty();

        Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        MinecraftServer server = player.getServer();
        return getGuild(server, member.getGuildKey());
    }

    public static GuildSettings getSettings(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (!state.doesGuildHaveSettings(guildName)) return GuildSettings.getDefaultSettings();

        return state.getSettings(guildName);
    }
}
