package keno.guildedparties.server;

import keno.guildedparties.server.compat.ServerGuildedCompatEntrypoint;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
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
