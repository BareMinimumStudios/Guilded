package keno.guildedparties.utils;

import keno.guildedparties.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * @see GuildServerPlayerApi#removePlayerFromGuild(MinecraftServer, String)
     * @see GuildServerPlayerApi#removePlayerFromGuild(MinecraftServer, UUID)
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

    /** Promotes the player if they're in a guild
     * @param player The player to be promoted
     * @return Whether the player was promoted successfully
     * @see GuildServerPlayerApi#promotePlayer(MinecraftServer, String)
     * @see GuildServerPlayerApi#promotePlayer(MinecraftServer, UUID) 
     * */
    public static boolean promotePlayer(ServerPlayerEntity player) {
        if (player != null && GuildPlayerAPI.isPlayerInGuild(player)) {
            AtomicBoolean successful = new AtomicBoolean(false);
            GuildApi.modifyGuildPersistentState(player.getServer(), state -> {
                String guild = GuildPlayerAPI.getPlayerData(player).orElseThrow().getGuildKey();
                successful.set(state.getGuild(guild).promoteMember(player) == 1);
            });
            return successful.get();
        }
        return false;
    }

    /**
     * @param server The server
     * @param playerName The player's name
     * @return Whether the player was promoted successfully
     * @see GuildServerPlayerApi#promotePlayer(ServerPlayerEntity)
     */
    public static boolean promotePlayer(MinecraftServer server, String playerName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return promotePlayer(player);
    }

    /**
     * @param server The server
     * @param playerId The player's UUID
     * @return whether the player was promoted successfully
     * @see GuildServerPlayerApi#promotePlayer(ServerPlayerEntity)
     */
    public static boolean promotePlayer(MinecraftServer server, UUID playerId) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        return promotePlayer(player);
    }

    /** Demotes the player if they're in a guild
     * @param player The player being demoted
     * @return Whether the player was demoted successfully
     * @see GuildServerPlayerApi#demotePlayer(MinecraftServer, String) 
     * @see GuildServerPlayerApi#demotePlayer(MinecraftServer, UUID) 
     */
    public static boolean demotePlayer(ServerPlayerEntity player) {
        if (player != null && GuildPlayerAPI.isPlayerInGuild(player)) {
            AtomicBoolean successful = new AtomicBoolean(false);
            String guild = GuildPlayerAPI.getPlayerData(player).orElseThrow().getGuildKey();
            GuildApi.modifyGuildPersistentState(player.getServer(), state
                    -> successful.set(state.getGuild(guild).demoteMember(player) == 1));
            return successful.get();
        }
        return false;
    }

    /**
     * @param server The server
     * @param playerName The player's name
     * @return Whether the player was demoted successfully
     * @see GuildServerPlayerApi#demotePlayer(ServerPlayerEntity)
     */
    public static boolean demotePlayer(MinecraftServer server, String playerName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return demotePlayer(player);
    }

    /**
     * @param server The server
     * @param playerId The player's UUID 
     * @return Whether the player was demoted successfully
     * @see GuildServerPlayerApi#demotePlayer(ServerPlayerEntity) 
     */
    public static boolean demotePlayer(MinecraftServer server, UUID playerId) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        return demotePlayer(player);
    }

    /** Changes the player's rank to the one stated
     * @param player The player
     * @param rankName The name of the rank stated
     * @return Whether the player's rank was changed successfully
     * @see GuildServerPlayerApi#changePlayerRank(MinecraftServer, String, String) 
     * @see GuildServerPlayerApi#changePlayerRank(MinecraftServer, UUID, String) 
     */
    public static boolean changePlayerRank(ServerPlayerEntity player, String rankName) {
        if (player != null && GuildPlayerAPI.isPlayerInGuild(player)) {
            AtomicBoolean successful = new AtomicBoolean(false);
            String guildName = GuildPlayerAPI.getPlayerData(player).orElseThrow().getGuildKey();
            GuildApi.modifyGuildPersistentState(player.getServer(), state
                    -> successful.set(state.getGuild(guildName).changeMemberRank(player, rankName) == 1));
            return successful.get();
        }
        return false;
    }

    /**
     * @param server The server
     * @param playerName The player's name
     * @param rankName The name of the player's new rank
     * @return Whether the player's rank was changed successfully
     * @see GuildServerPlayerApi#changePlayerRank(ServerPlayerEntity, String) 
     */
    public static boolean changePlayerRank(MinecraftServer server, String playerName, String rankName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return changePlayerRank(player, rankName);
    }

    /**
     * @param server The server
     * @param playerId The player's Id
     * @param rankName The name of the player's new rank
     * @return Whether the player's rank was changed successfully
     * @see GuildServerPlayerApi#changePlayerRank(ServerPlayerEntity, String) 
     */
    public static boolean changePlayerRank(MinecraftServer server, UUID playerId, String rankName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
        return changePlayerRank(player, rankName);
    }
}
