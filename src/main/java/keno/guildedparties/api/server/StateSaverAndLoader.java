package keno.guildedparties.api.server;

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer;
import com.bibireden.data_attributes.endec.nbt.NbtSerializer;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildBanList;
import keno.guildedparties.api.data.guilds.GuildSettings;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateSaverAndLoader extends PersistentState {
    private final HashMap<String, Guild> guilds = new HashMap<>();
    private final HashMap<String, GuildSettings> settings = new HashMap<>();
    private final HashMap<String, GuildBanList> banlists = new HashMap<>();

    private StateSaverAndLoader() {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        StringBuilder keySet = new StringBuilder();
        List<String> keys = new ArrayList<>(guilds.keySet());
        String lastKey = keys.stream().reduce((first, second) -> second).orElse(null);
        for (String key : keys) {
            NbtElement guild = Guild.ENDEC.encodeFully(NbtSerializer::of, getGuild(key));

            NbtElement setting = GuildSettings.ENDEC.encodeFully(NbtSerializer::of,
                    settings.containsKey(key) ? getSettings(key) : GuildSettings.getDefaultSettings());

            NbtElement banList = GuildBanList.ENDEC.encodeFully(NbtSerializer::of,
                    banlists.containsKey(key) ? getBanlist(key) : new GuildBanList());

            nbt.put(key, guild);
            nbt.put(key + "_settings", setting);
            nbt.put(key + "_banlist", banList);

            keySet.append(key);
            if (!key.equals(lastKey)) {
                keySet.append(",");
            }
        }
        nbt.putString("keySet", String.valueOf(keySet));
        return nbt;
    }

    public static StateSaverAndLoader createNew() {
        return new StateSaverAndLoader();
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        String keySet = tag.getString("keySet");
        String[] keys = keySet.split(",");
        if (!keySet.isBlank()) {
            for (String key : keys) {
                Guild guild = Guild.ENDEC.decodeFully(NbtDeserializer::of, tag.get(key));
                GuildSettings settings = GuildSettings.ENDEC.decodeFully(NbtDeserializer::of, tag.get(key + "_settings"));
                GuildBanList banlist = GuildBanList.ENDEC.decodeFully(NbtDeserializer::of, tag.get(key + "_banlist"));
                state.addGuild(guild);
                state.addSettings(settings, key);
                state.addBanlist(banlist, key);
            }
        }
        return state;
    }

    public static StateSaverAndLoader getStateFromServer(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        assert world != null;

        return world.getPersistentStateManager()
                .getOrCreate(StateSaverAndLoader::createFromNbt,
                        StateSaverAndLoader::createNew, "guildedparties:state");
    }

    public HashMap<String, Guild> getGuilds() {
        return guilds;
    }

    public boolean hasGuild(String guildName) {
        return this.guilds.containsKey(guildName);
    }

    public boolean doesGuildHaveSettings(String guildName) {
        return this.settings.containsKey(guildName);
    }

    public boolean doesGuildHaveBanlist(String guildName) {
        return this.banlists.containsKey(guildName);
    }

    public Guild getGuild(String guildName) {
        return this.guilds.get(guildName);
    }

    public GuildBanList getBanlist(String guildName) {
        return this.banlists.get(guildName);
    }

    public GuildSettings getSettings(String guildName) {
        return this.settings.get(guildName);
    }

    public void addGuild(Guild guild) {
        if (!this.guilds.containsKey(guild.getName())) {
            addGuild(guild, guild.getName());
        }
    }

    public void addGuild(Guild guild, String guildName) {
        if (!this.guilds.containsKey(guildName)) {
            this.guilds.put(guildName, guild);
        }
    }

    public void removeGuild(String guildName) {
        this.guilds.remove(guildName);
        this.settings.remove(guildName);
        this.banlists.remove(guildName);
        this.markDirty();
    }

    public void addSettings(GuildSettings settings, String guildName) {
        this.settings.put(guildName, settings);
    }

    public void addBanlist(GuildBanList banList, String guildName) {
        if (!this.banlists.containsKey(guildName)) {
            this.banlists.put(guildName, banList);
        }
    }
}
