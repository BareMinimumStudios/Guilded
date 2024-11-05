package keno.guildedparties.data;

import keno.guildedparties.data.guilds.Guild;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.HashMap;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<String, Guild> guilds = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        for (String key : guilds.keySet()) {

        }
        return null;
    }
}
