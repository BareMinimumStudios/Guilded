package keno.guildedparties.api;

import keno.guildedparties.impl.data.GPAttachmentTypes;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.GuildBanList;
import keno.guildedparties.impl.data.guilds.GuildSettings;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import keno.guildedparties.impl.data.player.Member;
import keno.guildedparties.api.events.items.GuildItemStorage;
import keno.guildedparties.impl.server.StateSaverAndLoader;
import keno.guildedparties.impl.utils.StateHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/** Api for guilds, should be used on serverside only */
@SuppressWarnings("UnstableApiUsage")
public class GuildApi {
    /** performs the {@code guildConsumer} lambda on all guilds in the server
     * @param guildConsumer the lambda performed on all guilds **/
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

    /** Send a message to all players in a guild.
     * See the overloads {@link #broadcastToGuildmates(MinecraftServer, Guild, String)} and
     * {@link #broadcastToGuildmates(MinecraftServer, String, ServerPlayerEntity)}
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

    /** Retrieves an optional containing a guild
     * @param guildName The name of the guild you're retrieving, sometimes called a "guildKey" internally
     * @return Optional that will contain a guild object, or be empty if the guild isn't found
     * @see Member
     * @see Guild
     * @see GuildApi#modifyGuildPersistentState(MinecraftServer, StateHandler) modifying guild data safely
     * @see GuildApi#getGuild(ServerPlayerEntity)
     * */
    public static Optional<Guild> getGuild(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (!state.hasGuild(guildName)) return Optional.empty();

        return Optional.of(state.getGuild(guildName));
    }

    /** Checks if a guild exists
     * see {@link #doesGuildExist(ServerPlayerEntity, String)}
     * @param name the guild's name
     * @return if the guild exists or not
     */
    public static boolean doesGuildExist(MinecraftServer server, String name) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        return state.hasGuild(name);
    }

    public static boolean doesGuildExist(ServerPlayerEntity player, String name) {
        return doesGuildExist(player.getServer(), name);
    }

    /** @see GuildApi#getGuild(MinecraftServer, String) **/
    public static Optional<Guild> getGuild(ServerPlayerEntity player) {
        if (player == null || !player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return Optional.empty();

        Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        MinecraftServer server = player.getServer();
        return getGuild(server, member.getGuildKey());
    }

    /** Gets a guild's server-side settings
     * @param guildName the guild's name
     * @return the guild's settings **/
    public static GuildSettings getSettings(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (!state.doesGuildHaveSettings(guildName)) return GuildSettings.getDefaultSettings();

        return state.getSettings(guildName);
    }

    /** Gets a guild's banlist
     * @param guildName guild's name
     * @return the guild's banlist
     */
    public static GuildBanList getBanList(MinecraftServer server, String guildName) {
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        return state.getBanlist(guildName);
    }

    /**
     * Returns an optional containing a guild's item list, which defines what items are whitelisted to that guild
     * @param guildId The guild's id
     * @return the guild's taglist, containing the guild-specific
     */
    public static Optional<GuildItemList> getGuildItems(@Nullable MinecraftServer server, Identifier guildId) {
        if (server != null) {
            GuildItemStorage storage = GuildItemStorage.instance();
            GuildItemList list = storage.getGuildTagList(guildId);
            if (list != null) {
                return Optional.of(list);
            }
        }
        return Optional.empty();
    }
}
