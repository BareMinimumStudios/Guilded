package keno.net.guilded_parties.server;

import keno.net.guilded_parties.guilds.data.Guild;
import keno.net.guilded_parties.utils.IdUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.RegistryKey;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DynamicRegistries.registerSynced(RegistryKey.ofRegistry(IdUtils.modLoc("guilds")), Guild.CODEC);
    }
}
