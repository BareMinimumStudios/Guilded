package keno.guildedparties.api.events.items;

import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class GuildItemEvents {
    /// Use this event to create item whitelists to specific guilds only
    public static final Event<AddGuildItemsCallback> ADD = EventFactory.createArrayBacked(AddGuildItemsCallback.class,
            (listeners) -> () -> {
                GuildItemStorage storage = GuildItemStorage.instance();
                for (AddGuildItemsCallback listener : listeners) {
                    ItemListContainer container = listener.add();
                    storage.addGuildTagList(container.guildName(), container.list());
                }
                return null;
            });

    /// Use this event to modify guild item whitelists
    public static final Event<ModifyGuildItemsCallback> MODIFY = EventFactory.createArrayBacked(ModifyGuildItemsCallback.class,
            (listeners) -> (id, list) -> {
                for (ModifyGuildItemsCallback listener : listeners) {
                    listener.modify(id, list);
                }
            });

    public interface AddGuildItemsCallback {
        ItemListContainer add();
    }

    public interface ModifyGuildItemsCallback {
        void modify(final String guildId, final GuildItemList list);
    }
}
