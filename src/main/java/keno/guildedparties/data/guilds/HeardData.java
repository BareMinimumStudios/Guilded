package keno.guildedparties.data.guilds;

import java.util.ArrayList;
import java.util.List;

/** Any data that's been "heard" (collected from resource listeners) are stored here for use*/
public class HeardData {
    private static ArrayList<Guild> guilds = new ArrayList<>();

    public static ArrayList<Guild> getGuilds() {
        return guilds;
    }

    public static void loadGuilds(List<Guild> guilds) {
        HeardData.guilds = new ArrayList<>(guilds);
    }
}
