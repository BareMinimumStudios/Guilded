package keno.guildedparties.events;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.items.GuildTagList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class GuildItemStorage {
    private static GuildItemStorage INSTANCE;

    private final Map<Identifier, GuildTagList> guildItems;
    private boolean freezeAddition = false;
    private boolean freezeModification = false;
    private boolean lockStorage = false;

    private GuildItemStorage(Map<Identifier, GuildTagList> guildItems) {
        this.guildItems = guildItems;
    }

    public void addGuildTagList(Identifier guild, GuildTagList list) {
        if (!lockStorage) {
            if (!freezeAddition) {
                if (!guildItems.containsKey(guild)) guildItems.put(guild, list);
            }
        }
    }

    public void modifyGuildTagList(Identifier guild, GuildTagList list, int flag) {
        if (!lockStorage) {
            if (!freezeModification) {
                if (guildItems.containsKey(guild)) {
                    if (flag == 0) guildItems.get(guild).addTags(list);
                    else if (flag == 1) guildItems.get(guild).subtractTags(list);
                    else throw new IllegalStateException("Invalid flag: " + flag);
                }
            }
        }
    }

    public GuildTagList getGuildTagList(Identifier guild) {
        if (!guildItems.containsKey(guild)) {
            GuildedParties.LOGGER.warn("This guild does not have a tag list: {}", guild);
            return null;
        }
        return guildItems.get(guild);
    }

    public List<Pair<Identifier, GuildTagList>> getLists() {
        Set<Identifier> ids = guildItems.keySet();
        List<Pair<Identifier, GuildTagList>> list = new ArrayList<>();
        for (Identifier id : ids) {
            list.add(new Pair<>(id, guildItems.get(id)));
        }
        return list;
    }

    protected void freezeAddition() {
        if (!this.lockStorage) {
            this.freezeAddition = true;
        }
    }

    protected void freezeModification() {
        if (!this.lockStorage) {
            this.freezeModification = true;
        }
    }

    protected void unfreezeModification() {
        if (!this.lockStorage) {
            this.freezeModification = false;
        }
    }

    protected void lock() {
        this.lockStorage = true;
    }

    public synchronized static GuildItemStorage instance() {
        if (INSTANCE == null) {
            INSTANCE = new GuildItemStorage(new HashMap<>());
        }
        return INSTANCE;
    }
}
