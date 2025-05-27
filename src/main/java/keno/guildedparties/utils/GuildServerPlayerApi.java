package keno.guildedparties.utils;

import keno.guildedparties.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.UUID;

/** Player API for server-side
 * @see GuildPlayerAPI common-side player api
 * @see GuildApi guild api **/
public class GuildServerPlayerApi {

    /** Adds a player to a guild
     * @param player Player to add
     * @param guildName Name of the guild
     * @return Whether the player was added successfully
     * @see GuildServerPlayerApi#addPlayerToGuild(MinecraftServer, String, String)
     * @see GuildServerPlayerApi#addPlayerToGuild(MinecraftServer, UUID, String) 
     */
    public static boolean addPlayerToGuild(ServerPlayerEntity player, String guildName) {
        if (player != null && GuildApi.doesGuildExist(player, guildName)) {
            if (GuildPlayerAPI.isPlayerInGuild(player)) {
                removePlayerFromGuild(player);
            }
            GuildApi.modifyGuildPersistentState(player.getServer(), state ->
                    state.getGuild(guildName).addPlayerToGuild(player, "Recruit"));
            return true;
        }
        return false;
    }

    /**
     * @param server The server
     * @param playerName The player's name
     * @param guildName Name of the guild
     * @return Whether the player was added successfully
     * @see GuildServerPlayerApi#addPlayerToGuild(ServerPlayerEntity, String)
     */
    public static boolean addPlayerToGuild(MinecraftServer server, String playerName, String guildName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return addPlayerToGuild(player, guildName);
    }

    /**
     * @param server The server
     * @param playerId The player's UUID
     * @param guildName Name of the guild
     * @return Whether the player was added successfully
     * @see GuildServerPlayerApi#addPlayerToGuild(ServerPlayerEntity, String)
     */
    public static boolean addPlayerToGuild(MinecraftServer server, UUID playerId, String guildName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        return addPlayerToGuild(player, guildName);
    }

    /** Removes the player from their guild, if in one
     * @param player Player that is removed from a guild
     * @return Whether the player was removed successfully
     */
    public static boolean removePlayerFromGuild(ServerPlayerEntity player) {
        if (player != null) {
            if (GuildPlayerAPI.isPlayerInGuild(player)) {
                Member member = GuildPlayerAPI.getPlayerData(player).orElseThrow();
                GuildApi.modifyGuildPersistentState(player.getServer(), state ->
                        state.getGuild(member.getGuildKey()).removePlayerFromGuild(player));
                return true;
            }
        }
        return false;
    }

    /**
     * @param server The server
     * @param playerName The player's name
     * @return Whether the player was removed successfully
     * @see GuildServerPlayerApi#removePlayerFromGuild(ServerPlayerEntity)
     */
    public static boolean removePlayerFromGuild(MinecraftServer server, String playerName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return removePlayerFromGuild(player);
    }

    /**
     * @param server The server
     * @param playerId The player's UUID
     * @return Whether the player was removed successfully
     * @see GuildServerPlayerApi#removePlayerFromGuild(ServerPlayerEntity)
     */
    public static boolean removePlayerFromGuild(MinecraftServer server, UUID playerId) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        return removePlayerFromGuild(player);
    }
}
