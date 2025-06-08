package keno.guildedparties.api.events;

import keno.guildedparties.data.guilds.Guild;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class GuildEvents {
    /// Called when checking if a guild can be created
    public static final Event<CanCreateGuildCallback> CAN_CREATE_GUILD = EventFactory.createArrayBacked(CanCreateGuildCallback.class,
            (listeners) -> (player, guild) -> {
                for (CanCreateGuildCallback listener : listeners) {
                    if (!listener.canCreateGuild(player, guild)) return false;
                }
                return true;
            });

    /// Called on guild creation
    public static final Event<OnGuildCreationCallback> ON_GUILD_CREATION = EventFactory.createArrayBacked(OnGuildCreationCallback.class,
            (listeners) -> (player, guild) -> {
                for (OnGuildCreationCallback listener : listeners) {
                    listener.onGuildCreation(player, guild);
                }
            });

    /// Called on guild disbanding
    public static final Event<OnGuildClosureCallback> ON_GUILD_CLOSURE = EventFactory.createArrayBacked(OnGuildClosureCallback.class,
            (listeners) -> (player, guild) -> {
                for (OnGuildClosureCallback listener : listeners) {
                    listener.onGuildClosure(player, guild);
                }
            });

    public interface CanCreateGuildCallback {
        boolean canCreateGuild(ServerPlayerEntity leader, Guild guild);
    }

    public interface OnGuildCreationCallback {
        void onGuildCreation(ServerPlayerEntity leader, Guild guild);
    }

    public interface OnGuildClosureCallback {
        void onGuildClosure(ServerPlayerEntity leader, Guild guild);
    }
}
