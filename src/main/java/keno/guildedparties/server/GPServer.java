package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.server.commands.GPCommandRegistry;
import keno.guildedparties.server.compat.ServerGuildedCompatEntrypoint;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

import static keno.guildedparties.GuildedParties.GUILD_REGISTRY;
import static keno.guildedparties.GuildedParties.SETTINGS_REGISTRY;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DynamicRegistries.registerSynced(GUILD_REGISTRY, Guild.codec, Guild.codec);
        DynamicRegistries.registerSynced(SETTINGS_REGISTRY, GuildSettings.codec, GuildSettings.codec);

        ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);
        ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);

        ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, GuildedParties::addGuildNote);

        GPCommandRegistry.init(true);
        GPNetworking.init(true);
        initializeCompatEntrypoint();
    }

    public void initializeCompatEntrypoint() {
        List<EntrypointContainer<ServerGuildedCompatEntrypoint>> containers
                = FabricLoader.getInstance().getEntrypointContainers("guilded_server", ServerGuildedCompatEntrypoint.class);

        for (EntrypointContainer<ServerGuildedCompatEntrypoint> container : containers) {
            ServerGuildedCompatEntrypoint entrypoint = container.getEntrypoint();
            entrypoint.initServer();
        }
    }
}
