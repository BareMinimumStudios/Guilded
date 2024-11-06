package keno.guildedparties;

import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.server.StateSaverAndLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guildedparties";
	public static RegistryKey<Registry<Guild>> GUILD_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "guilds"));

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(GUILD_REGISTRY, Guild.codec, Guild.codec);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
			Registry<Guild> registry = server.getRegistryManager().getOrThrow(GUILD_REGISTRY);
			registry.stream().iterator().forEachRemaining(guild -> {
				LOGGER.info("Guild name {}", guild.getName());
				if (!state.guilds.containsKey(guild.getName())) {
					state.guilds.put(guild.getName(), guild);
					LOGGER.info("guild name {}", state.guilds.get(guild.getName()).getName());
				}
			});
		});

		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer)
				-> LOGGER.info(serverPlayNetworkHandler.player.getUuid().toString()));
	}

	public static Identifier GPLoc(String path) {
		return Identifier.of(GuildedParties.MOD_ID, path);
	}
}