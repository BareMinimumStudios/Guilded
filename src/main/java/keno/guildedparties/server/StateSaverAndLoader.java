package keno.guildedparties.server;

import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildBanList;
import keno.guildedparties.data.guilds.GuildSettings;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateSaverAndLoader extends PersistentState {
    private final HashMap<String, Guild> guilds = new HashMap<>();
    // Use the guild's internal name to get its banlist
    private final HashMap<String, GuildBanList> banLists = new HashMap<>();
    private final HashMap<String, GuildSettings> guildSettingsMap = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        StringBuilder keySet = new StringBuilder();
        List<String> keys = new ArrayList<>(guilds.keySet());
        for (String key : keys) {
            NbtElement guild = Guild.endec.encodeFully(NbtSerializer::of, getGuild(key));
            NbtElement banList;
            NbtElement settings;
            if (banLists.containsKey(key)) {
                banList = GuildBanList.codec.encodeStart(NbtOps.INSTANCE, banLists.get(key)).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            } else {
                GuildBanList list = new GuildBanList(new ArrayList<>());
                banList = GuildBanList.codec.encodeStart(NbtOps.INSTANCE, list).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            }

            if (guildSettingsMap.containsKey(key)) {
                settings = GuildSettings.codec.encodeStart(NbtOps.INSTANCE, getSettings(key)).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            } else {
                GuildSettings guildSettings = new GuildSettings(false, 5, 3, 3, 5);
                settings = GuildSettings.codec.encodeStart(NbtOps.INSTANCE, guildSettings).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            }
            nbt.put(key, guild);
            nbt.put(key + "_banlist", banList);
            nbt.put(key + "_settings", settings);
            keySet.append(key);
            if (!key.equals(keys.getLast())) {
                keySet.append(",");
            }
        }
        nbt.putString("keySet", String.valueOf(keySet));
        return nbt;
    }

    public HashMap<String, Guild> getGuilds() {
        return guilds;
    }

    public boolean hasGuild(String guildName) {
        return this.guilds.containsKey(guildName);
    }

    public boolean doesGuildHaveSettings(String guildName) {
        return this.guildSettingsMap.containsKey(guildName);
    }

    public boolean doesGuildHaveBanlist(String guildName) {
        return this.banLists.containsKey(guildName);
    }

    public Guild getGuild(String guildName) {
        return this.guilds.get(guildName);
    }

    public GuildBanList getBanlist(String guildName) {
        return this.banLists.get(guildName);
    }

    public GuildSettings getSettings(String guildName) {
        return this.guildSettingsMap.get(guildName);
    }

    public void removeGuild(String guildName) {
        this.guilds.remove(guildName);
        this.guildSettingsMap.remove(guildName);
        this.banLists.remove(guildName);
        this.markDirty();
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

    public void addSettings(GuildSettings settings, String guildName) {
        this.guildSettingsMap.put(guildName, settings);
    }



    public void addBanlist(GuildBanList banList, String guildName) {
        if (!this.banLists.containsKey(guildName)) {
            this.banLists.put(guildName, banList);
        }
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        StateSaverAndLoader saverAndLoader = new StateSaverAndLoader();
        String keySet = tag.getString("keySet");
        String[] keys = keySet.split(",");
        if (!keySet.isBlank()) {
            for (String key : keys) {
                Guild guild = Guild.endec.decodeFully(NbtDeserializer::of, tag.get(key));
                GuildSettings settings = GuildSettings.codec.parse(NbtOps.INSTANCE, tag.get(key + "_settings")).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
                GuildBanList list = GuildBanList.codec.parse(NbtOps.INSTANCE, tag.get(key + "_banlist")).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
                saverAndLoader.guilds.put(key, guild);
                saverAndLoader.banLists.put(key, list);
                saverAndLoader.guildSettingsMap.put(key, settings);
            }
        }
        return saverAndLoader;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null);

    public static StateSaverAndLoader getStateFromServer(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        StateSaverAndLoader saverAndLoader = manager.getOrCreate(type, GuildedParties.MOD_ID);
        saverAndLoader.markDirty();
        return saverAndLoader;
    }
}
