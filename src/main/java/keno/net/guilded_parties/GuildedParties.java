package keno.net.guilded_parties;

import keno.net.guilded_parties.guilds.data.Guild;
import keno.net.guilded_parties.server.persistant_state.GuildSaverAndLoader;
import keno.net.guilded_parties.utils.PacketIds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

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
			Stream<Guild> registryStream = registry.stream();
			registryStream = registryStream.filter(registeredGuild ->
					(state.guilds.stream().noneMatch(savedGuild -> savedGuild.id().compareTo(registeredGuild.id()) == 0)));
			registryStream.forEach(guild -> state.guilds.add(guild));
			state.guilds.stream().distinct().forEach(guild -> LOGGER.info(guild.toString()));
		});

		ServerPlayNetworking.registerGlobalReceiver(PacketIds.VIEW_GUILDS, (server, player, handler, buf, responseSender) -> {
			LOGGER.info("Received Packet!");
			Identifier guildId = buf.readIdentifier();
			LOGGER.info(guildId.toString());
			Guild guild = server.getRegistryManager().get(GUILD_REGISTRY).get(guildId);
			if (guild != null) {
				LOGGER.info("Found guild!");
				String numberOfRanks = Integer.toString(guild.ranks().size());
				String numberOfMembers = Integer.toString(guild.members().size());
				String message = guildId.toString() +
						" found! Number of ranks: " + numberOfRanks + " Number of Members: " + numberOfMembers;
				player.sendMessage(Text.of(message));
			}
		});
	}
}