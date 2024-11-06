package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
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
    public HashMap<String, Guild> guilds = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        StringBuilder keySet = new StringBuilder();
        List<String> keys = new ArrayList<>(guilds.keySet());
        for (String key : keys) {
            NbtElement guild = Guild.codec.encodeStart(NbtOps.INSTANCE, guilds.get(key)).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();

            nbt.put(key, guild);
            if (!key.equals(keys.getLast())) {
                keySet.append(key);
                keySet.append(",");
            }
        }
        GuildedParties.LOGGER.info(String.valueOf(keySet));
        nbt.putString("keySet", String.valueOf(keySet));
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        StateSaverAndLoader saverAndLoader = new StateSaverAndLoader();
        String keySet = tag.getString("keySet");
        String[] keys = keySet.split(",");
        if (!keySet.isBlank()) {
            for (String key : keys) {
                Guild guild = Guild.codec.parse(NbtOps.INSTANCE, tag.get(key)).resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
                saverAndLoader.guilds.put(key, guild);
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
