package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.config.GPServerConfig;
import keno.guildedparties.data.listeners.GuildResourceListener;
import keno.guildedparties.data.listeners.GuildSettingsResourceListener;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.server.commands.GPCommandRegistry;
import keno.guildedparties.server.compat.ServerGuildedCompatEntrypoint;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resource.ResourceType;

import java.util.List;

public class GPServer implements DedicatedServerModInitializer {
    public static final GPServerConfig CONFIG = GPServerConfig.createAndLoad();

    @Override
    public void onInitializeServer() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildResourceListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildSettingsResourceListener());

        ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);
        ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);

        ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, GuildedParties::addGuildNote);

        GPCommandRegistry.init(true);
        GPNetworking.init();
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
