package keno.guildedparties.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class GuildItemEvents {
    /// Use this event to create item whitelists to specific guilds only
    public static final Event<AddGuildItemsCallback> ADD = EventFactory.createArrayBacked(AddGuildItemsCallback.class,
            (listeners) -> (storage) -> {
                storage.freezeModification();
                for (AddGuildItemsCallback listener : listeners) {
                    listener.add(storage);
                }
                storage.freezeAddition();
                storage.unfreezeModification();
            });

    /// Use this event to modify guild item whitelists
    public static final Event<ModifyGuildItemsCallback> MODIFY = EventFactory.createArrayBacked(ModifyGuildItemsCallback.class,
            (listeners) -> (storage) -> {
                for (ModifyGuildItemsCallback listener : listeners) {
                    listener.modify(storage);
                }
                storage.freezeModification();
                storage.lock();
            });

    public interface AddGuildItemsCallback {
        void add(final GuildItemStorage storage);
    }

    public interface ModifyGuildItemsCallback {
        void modify(final GuildItemStorage storage);
    }
}
