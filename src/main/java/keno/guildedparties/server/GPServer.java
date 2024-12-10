package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.server.commands.GPCommandRegistry;
import keno.guildedparties.server.compat.ServerGuildedCompatEntrypoint;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class GPServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);

        ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, GuildedParties::addGuildNote);

        GPCommandRegistry.init();
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
