package keno.guildedparties.api.events;


import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.Rank;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class GuildPlayerEvents {
    /// Called when a player joins a guild
    public static final Event<OnGuildJoinCallback> ON_GUILD_JOIN = EventFactory
            .createArrayBacked(OnGuildJoinCallback.class, (listeners) -> (player, guild) -> {
                for (OnGuildJoinCallback listener : listeners) {
                    listener.onGuildJoin(player, guild);
                }
            });

    /// Called when checking if a player can join a guild
    public static final Event<CanJoinGuildCallback> CAN_JOIN_GUILD = EventFactory
            .createArrayBacked(CanJoinGuildCallback.class, (listeners) -> (player, guild) -> {
                for (CanJoinGuildCallback listener : listeners) {
                    if (!listener.canJoinGuild(player, guild)) return false;
                }
                return true;
            });

    /// Called when a player's rank changes
    public static final Event<OnRankChangeCallback> ON_RANK_CHANGE = EventFactory
            .createArrayBacked(OnRankChangeCallback.class, (listeners) -> (player, rank, guild) -> {
                for (OnRankChangeCallback listener : listeners) {
                    listener.onRankChange(player, rank, guild);
                }
            });

    /// Called when a player leaves a guild
    public static final Event<OnLeavingGuildCallback> ON_LEAVING_GUILD = EventFactory
            .createArrayBacked(OnLeavingGuildCallback.class, (listeners) -> (player, rank, guild) -> {
                for (OnLeavingGuildCallback listener : listeners) {
                    listener.onLeavingGuild(player, rank, guild);
                }
            });

    public interface OnGuildJoinCallback {
        void onGuildJoin(final ServerPlayerEntity player, final Guild guild);
    }

    public interface CanJoinGuildCallback {
        boolean canJoinGuild(final ServerPlayerEntity player, final Guild guild);
    }

    public interface OnRankChangeCallback {
        void onRankChange(final ServerPlayerEntity player,
                          final Rank rank, final Guild guild);
    }

    public interface OnLeavingGuildCallback {
        void onLeavingGuild(final ServerPlayerEntity player,
                            final Rank rank, final Guild guild);
    }
}
