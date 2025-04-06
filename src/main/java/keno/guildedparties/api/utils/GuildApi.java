package keno.guildedparties.api.utils;

import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildBanList;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.data.player.attachments.MemberComponent;
import keno.guildedparties.api.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Consumer;

public final class GuildApi {
    public static void forEachGuildInServer(MinecraftServer server, Consumer<Guild> guildConsumer) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        state.getGuilds().values().forEach(guildConsumer);
        state.markDirty();
    }

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
        Text message = Text.of("[GC] ").copy().append(text).setStyle(Style.EMPTY.withColor(0xffffcc00));
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

        if (GPComponents.MEMBER_KEY.isProvidedBy(sender)) {
            MemberComponent component = GPComponents.MEMBER_KEY.get(sender);
            if (component.hasMemberData()) {
                StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
                Member member = component.getMemberData();
                Guild guild = state.getGuild(member.getGuildKey());
                String rankName = "[%s][%s] ".formatted(sender.getGameProfile().getName(), member.getRank().name());
                message = rankName + message;
                broadcastToGuildmates(server, guild, message);
            }
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

    public static boolean doesGuildExist(MinecraftServer server, String name) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        return state.hasGuild(name);
    }

    public static boolean doesGuildExist(ServerPlayerEntity player, String name) {
        return doesGuildExist(player.getServer(), name);
    }

    /** Overload of GuildUtils#getGuild that uses a ServerPlayerEntity instead for simplicity
     * @param player The player to retrieve a guild object from, via their Member data
     * @see Member
     * @see Guild
     * @see GuildApi#getGuild(MinecraftServer, String)
     * */
    public static Optional<Guild> getGuild(ServerPlayerEntity player) {
        if (player == null || !GPComponents.MEMBER_KEY.isProvidedBy(player)) {
            return Optional.empty();
        }

        MemberComponent component = GPComponents.MEMBER_KEY.get(player);
        if (component.hasMemberData()) {
            Member member = component.getMemberData();
            MinecraftServer server = player.getServer();
            return getGuild(server, member.getGuildKey());
        }
        return Optional.empty();
    }

    public static GuildSettings getSettings(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (!state.doesGuildHaveSettings(guildName)) return GuildSettings.getDefaultSettings();

        return state.getSettings(guildName);
    }

    public static GuildBanList getBanList(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        return state.getBanlist(guildName);
    }

    public static void addPlayerToGuild(ServerPlayerEntity player, String guildName) {
        modifyGuildPersistentState(player.getServer(), state ->
                state.getGuild(guildName).addPlayerToGuild(player, "Recruit"));
    }
}
