package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.server.commands.GPCommandRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DynamicRegistries.registerSynced(GuildedParties.GUILD_REGISTRY, Guild.codec, Guild.codec);

        ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);
        ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);

        GPCommandRegistry.init();
    }
}
