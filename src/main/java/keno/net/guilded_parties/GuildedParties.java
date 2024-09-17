package keno.net.guilded_parties;

import keno.net.guilded_parties.guilds.Guild;
import keno.net.guilded_parties.server.persistant_state.GuildSaverAndLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guilded_parties";
	// public static HashMap<String, Guild> GUILDS = new HashMap<>();
	public static RegistryKey<Registry<Guild>> GUILD_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "guilds"));

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		DynamicRegistries.registerSynced(GUILD_REGISTRY, Guild.CODEC, Guild.CODEC);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			GuildSaverAndLoader state = GuildSaverAndLoader.getServerState(server);
			Registry<Guild> registry = server.getRegistryManager().get(GUILD_REGISTRY);
			registry.stream().distinct().filter(guild -> !state.guilds.contains(guild)).forEach(state.guilds::add);
			state.guilds.stream().distinct().forEach(guild -> LOGGER.info(guild.toString()));
		});
	}
}