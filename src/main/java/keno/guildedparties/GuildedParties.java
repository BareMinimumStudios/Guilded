package keno.guildedparties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keno.guildedparties.api.compat.GuildedCompatEntrypoint;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.guilds.GuildBanList;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.data.listeners.GuildResourceListener;
import keno.guildedparties.api.data.listeners.GuildSettingsResourceListener;
import keno.guildedparties.api.data.listeners.HeardData;
import keno.guildedparties.api.networking.GPNetworking;
import keno.guildedparties.api.server.StateSaverAndLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guildedparties";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Gson GSON = new GsonBuilder().create();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildResourceListener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildSettingsResourceListener());

		ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);

		GPNetworking.init();
		initializeCompatEntrypoint();
	}

	public void initializeCompatEntrypoint() {
		GuildedParties.LOGGER.info("Initializing compatibilities");

		FabricLoader.getInstance().getEntrypointContainers("guilded", GuildedCompatEntrypoint.class).forEach(container -> {
			GuildedCompatEntrypoint entrypoint = container.getEntrypoint();
			entrypoint.init();
		});
	}

	public static void fillPersistentState(MinecraftServer server) {
		GuildedParties.LOGGER.info("Loading data-driven guilds onto {} server", server.isDedicated() ? "dedicated" : "integrated");
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		final HashMap<String, Guild> guilds = HeardData.getGuilds();
		final HashMap<String, GuildSettings> guildSettings = HeardData.getGuildSettings();

		for (String fileName : guilds.keySet()) {
			Guild guild = guilds.get(fileName);
			if (!state.hasGuild(guild.getName())) {
				if (!guild.getName().contains(Character.toString(','))) {
					state.addGuild(guild);
				} else {
					GuildedParties.LOGGER.warn("Could not add '{}' due to containing an illegal character \nThe guild's name cannot contain a ','", guild.getName());
					return;
				}
			}
			if (!state.doesGuildHaveSettings(guild.getName())) {
				GuildSettings settings;
				if (guildSettings.containsKey(fileName)) {
					settings = guildSettings.get(fileName);
				} else {
					GuildedParties.LOGGER.warn("Guild {} lacks a settings json, generating a default. \nIf one is present, the file name must be the same as the guild json", guild.getName());
					settings = GuildSettings.getDefaultSettings();
				}
				state.addSettings(settings, guild.getName());
			}
			if (!state.doesGuildHaveBanlist(guild.getName())) {
				state.addBanlist(new GuildBanList(new ArrayList<>()), guild.getName());
			}
		}
		state.markDirty();
	}

	public static Identifier modLoc(String path) {
		return Identifier.of(MOD_ID, path);
	}
}