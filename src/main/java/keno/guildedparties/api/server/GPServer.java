package keno.guildedparties.api.server;

import keno.guildedparties.api.compat.dedicated.ServerGuildedCompatEntrypoint;
import keno.guildedparties.api.server.commands.GPCommandRegistry;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment)
                -> GPCommandRegistry.init(commandDispatcher, commandRegistryAccess, registrationEnvironment, true));

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
