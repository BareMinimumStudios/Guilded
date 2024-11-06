package keno.guildedparties.server;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class GPServer implements DedicatedServerModInitializer {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeServer() {
        DynamicRegistries.registerSynced(GuildedParties.GUILD_REGISTRY, Guild.codec, Guild.codec);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // Add any data-registered guilds to the state
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            Registry<Guild> registry = server.getRegistryManager().getOrThrow(GuildedParties.GUILD_REGISTRY);
            registry.stream().iterator().forEachRemaining(guild -> {
                if (!state.guilds.containsKey(guild.getName())) {
                    state.guilds.put(guild.getName(), guild);
                }
            });
        });

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            // Sync guild data to player attachment
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(minecraftServer);
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            UUID playerId = player.getUuid();
            if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                for (Guild guild : state.guilds.values()) {
                    if (guild.players.containsKey(playerId)) {
                        player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT,
                                new Member(guild.getName(), guild.players.get(playerId)));
                        break;
                    }
                }
            }
        });
    }
}
