package keno.guildedparties.data.listeners;

import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;

import java.util.HashMap;
import java.util.Map;

/** Any data that's been "heard" (collected from resource listeners) are stored here for use*/
public class HeardData {
    private static HashMap<String, Guild> guilds = new HashMap<>();
    private static HashMap<String, GuildSettings> guildSettings = new HashMap<>();

    public static HashMap<String, Guild> getGuilds() {
        return guilds;
    }

    /** Loads the hashmap containing
     * @param guilds a map where the keys are filenames,*/
    protected static void loadGuilds(Map<String, Guild> guilds) {
        HeardData.guilds = new HashMap<>(guilds);
    }

    public static HashMap<String, GuildSettings> getGuildSettings() {
        return guildSettings;
    }

    protected static void loadGuildSettings(Map<String, GuildSettings> map) {
        HeardData.guildSettings = new HashMap<>(map);
    }
}
