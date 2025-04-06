package keno.guildedparties.api.data.listeners;

import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildSettings;

import java.util.HashMap;
import java.util.Set;

public class HeardData {
    private static HashMap<String, Guild> guilds = new HashMap<>();
    private static HashMap<String, GuildSettings> guildSettings = new HashMap<>();

    public static HashMap<String, Guild> getGuilds() {
        return guilds;
    }

    public static Set<String> getGuildNames() {
        return guilds.keySet();
    }

    public static HashMap<String, GuildSettings> getGuildSettings() {
        return guildSettings;
    }

    /** Loads the hashmap containing
     * @param guilds a map where the keys are filenames,*/
    protected static void loadGuilds(HashMap<String, Guild> guilds) {
        HeardData.guilds = guilds;
    }

    protected static void loadGuildSettings(HashMap<String, GuildSettings> guildSettings) {
        HeardData.guildSettings = guildSettings;
    }
}
