package keno.net.guilded_parties.server.persistant_state;

import com.mojang.serialization.DataResult;
import keno.net.guilded_parties.GuildedParties;
import keno.net.guilded_parties.guilds.data.Guild;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashSet;

public class GuildSaverAndLoader extends PersistentState {
    public HashSet<Guild> guilds = new HashSet<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        int iteration = 0;
        for (Guild guild : guilds) {
            DataResult<NbtElement> result =  Guild.CODEC.encodeStart(NbtOps.INSTANCE, guild);
            NbtElement element = result.resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            nbt.put("guild_" + iteration, element);
            iteration++;
        }
        return nbt;
    }

    public static GuildSaverAndLoader createFromNbt(NbtCompound tag) {
        int iteration = 0;
        GuildSaverAndLoader state = new GuildSaverAndLoader();
        while (tag.contains("guild_" + iteration)) {
            NbtElement element = tag.get("guild_" + iteration);
            DataResult<Guild> result = Guild.CODEC.parse(NbtOps.INSTANCE, element);
            Guild guild = result.resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
            state.guilds.add(guild);
            iteration++;
        }
        return state;
    }

    private static Type<GuildSaverAndLoader> type = new Type<>(
            GuildSaverAndLoader::new,
            GuildSaverAndLoader::createFromNbt,
            null
    );

    public static GuildSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        GuildSaverAndLoader state = manager.getOrCreate(type, GuildedParties.MOD_ID);

        state.markDirty();

        return state;
    }
}
