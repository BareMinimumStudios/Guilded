package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DynamicRegistries.registerSynced(GuildedParties.GUILD_REGISTRY, Guild.codec, Guild.codec);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            Registry<Guild> registry = server.getRegistryManager().getOrThrow(GuildedParties.GUILD_REGISTRY);
            registry.stream().iterator().forEachRemaining(guild -> {
                if (!state.guilds.containsKey(guild.getName())) {
                    state.guilds.put(guild.getName(), guild);
                }
            });
        });
    }
}
