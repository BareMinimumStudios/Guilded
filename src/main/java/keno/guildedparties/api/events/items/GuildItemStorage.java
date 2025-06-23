package keno.guildedparties.api.events.items;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

import static keno.guildedparties.GuildedParties.CONFIG;

public class GuildItemStorage {
    private static GuildItemStorage INSTANCE;

    private final Map<String, GuildItemList> guildItems;
    private boolean freezeAddition = false;
    private boolean freezeModification = false;
    private boolean lockStorage = false;

    private GuildItemStorage(Map<String, GuildItemList> guildItems) {
        this.guildItems = guildItems;
    }

    public int addGuildTagList(String guild, GuildItemList list) {
        if (!lockStorage) {
            if (!freezeAddition) {
                if (!guildItems.containsKey(guild)) {
                    guildItems.put(guild, list);
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     *
     * @param guild name of the guild
     * @param list contains the ids of items to restrict to the guild
     * @param flag If 0, combine existing list with passed list additively. If 1, combine subtractively
     * @return 1 if successful, and 0 if not
     * @throws IllegalStateException if the flag is not 0 or 1
     */
    public int modifyGuildTagList(String guild, GuildItemList list, int flag) {
        if (!lockStorage) {
            if (!freezeModification) {
                if (guildItems.containsKey(guild)) {
                    if (flag == 0) {
                        guildItems.get(guild).addIds(list);
                        return 1;
                    }
                    else if (flag == 1) {
                        guildItems.get(guild).removeIds(list);
                        return 1;
                    }
                    else throw new IllegalStateException("Invalid flag: " + flag);
                }
            }
        }
        return 0;
    }

    public GuildItemList getGuildTagList(String guild) {
        if (!guildItems.containsKey(guild)) {
            GuildedParties.LOGGER.warn("This guild does not have a id list: {}", guild);
            return null;
        }
        return guildItems.get(guild);
    }

    public List<Pair<String, GuildItemList>> getLists() {
        Set<String> ids = guildItems.keySet();
        List<Pair<String, GuildItemList>> list = new ArrayList<>();
        for (String id : ids) {
            list.add(new Pair<>(id, guildItems.get(id)));
        }
        return list;
    }

    private void freezeAddition() {
        if (!this.lockStorage) {
            this.freezeAddition = true;
        }
    }

    private void freezeModification() {
        if (!this.lockStorage) {
            this.freezeModification = true;
        }
    }

    protected void unfreezeModification() {
        if (!this.lockStorage) {
            this.freezeModification = false;
        }
    }

    private void lock() {
        this.lockStorage = true;
    }

    public synchronized static GuildItemStorage instance() {
        if (INSTANCE == null) {
            INSTANCE = new GuildItemStorage(new HashMap<>());
        }
        return INSTANCE;
    }

    /// Yes, setting up guild items via config is this painful internally
    private void processConfigLists() {
        GuildedParties.LOGGER.info("Processing guild items in config");
        List<String> guildsWithItems = CONFIG.guildsWithItems();
        List<String> guildItems = CONFIG.guildItems();
        if (!guildsWithItems.isEmpty()) {
            if (guildItems.isEmpty()) throw new NullPointerException("Config field 'guildItems' is empty, despite field 'guildsWithItems' being full");
            List<List<String>> guildIds = guildsWithItems.stream().map(this::splitString).toList();
            List<List<Identifier>> itemIds = guildItems.stream().map(this::stringToIds).toList();

            for (List<String> names : guildIds) {
                int index = guildIds.indexOf(names);
                List<Identifier> ids = itemIds.get(index);
                for (String name : names) {
                    UUID id = UUID.randomUUID();
                    GuildItemList list = new GuildItemList(id, ids);
                    if (!this.guildItems.containsKey(name)) this.guildItems.put(name, list);
                }
            }
        }
    }



    private Identifier stringToId(String string) {
        return Identifier.of(string);
    }

    private List<String> splitString(String string) {
        return List.of(string.split(","));
    }

    private List<Identifier> stringToIds(String string) {
        List<Identifier> ids = new ArrayList<>();
        String[] splitString = string.split(",");
        for (String str : splitString) {
            ids.add(stringToId(str));
        }
        return ids;
    }

    public void handleAddition() {
        freezeModification();
        GuildItemEvents.ADD.invoker().add();
        processConfigLists();
        freezeAddition();
        unfreezeModification();
    }

    public void handleModification() {
        Set<String> ids = this.guildItems.keySet();
        for (String guildName : ids) {
            GuildItemEvents.MODIFY.invoker().modify(guildName, this.guildItems.get(guildName));
        }

        freezeModification();
        lock();
    }
}
