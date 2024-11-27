package keno.guildedparties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import keno.guildedparties.compat.GuildedCompatEntrypoint;
import keno.guildedparties.config.GPConfig;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.listeners.GuildSettingsResourceListener;
import keno.guildedparties.data.listeners.HeardData;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildBanList;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.listeners.GuildResourceListener;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.server.commands.GPCommandRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("UnstableApiUsage")
public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guildedparties";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final GPConfig CONFIG = GPConfig.createAndLoad();
	public static final Gson GSON = new GsonBuilder().create();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildResourceListener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GuildSettingsResourceListener());
		
		GPAttachmentTypes.init();
		GPCommandRegistry.init(false);

		GPNetworking.init();

		ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);
		ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);

		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, GuildedParties::addGuildNote);

		initializeCompatEntrypoint();
	}

	public void initializeCompatEntrypoint() {
		GuildedParties.LOGGER.info("Initializing compatibilities");

		FabricLoader.getInstance().getEntrypointContainers("guilded", GuildedCompatEntrypoint.class).forEach(container -> {
			GuildedCompatEntrypoint entrypoint = container.getEntrypoint();
			entrypoint.init();
		});
	}

	public static Text addGuildNote(ServerPlayerEntity player, Text text) {
		if (player != null) {
			if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
				if (!player.getAttachedOrCreate(GPAttachmentTypes.GC_TOGGLE_ATTACHMENT)) {
					Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
					Text note = Text.of("[%s] ".formatted(member.getGuildKey()));
					return note.copy().append(text);
				}
			}
		}
		return text;
	}

	/** Ensures data-driven objects are in the persistent state */
	public static void fillPersistentState(MinecraftServer server) {
		// Add any data-registered guilds to the state
		GuildedParties.LOGGER.info("Loading data-driven guilds onto {} server", server.isDedicated() ? "dedicated" : "integrated");
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		final HashMap<String, Guild> guilds = HeardData.getGuilds();
		final HashMap<String, GuildSettings> guildSettings = HeardData.getGuildSettings();
		guilds.keySet().iterator().forEachRemaining(file_name -> {
			Guild guild = guilds.get(file_name);
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
				if (guildSettings.containsKey(file_name)) {
					settings = guildSettings.get(file_name);
				} else {
					GuildedParties.LOGGER.warn("Guild {} lacks a settings json, generating a default. \nIf one is present, the file name must be the same as the guild json", guild.getName());
					settings = new GuildSettings(false, 5, 3, 3, 5);
				}
				state.addSettings(settings, guild.getName());
			}
			if (!state.doesGuildHaveBanlist(guild.getName())) {
				state.addBanlist(new GuildBanList(new ArrayList<>()), guild.getName());
			}
		});
		state.markDirty();
	}

	/** Ensures player data and server data matches up */
	public static void syncAndInitializePlayerData(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
		// Sync guild data to player attachment
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		ServerPlayerEntity player = handler.getPlayer();
		String username = player.getGameProfile().getName();
		boolean isInGuild = false;


		for (Guild guild : state.getGuilds().values()) {
			if (state.doesGuildHaveBanlist(guild.getName())) {
				if (state.getBanlist(guild.getName()).isPlayerBanned(username)) continue;
			}

			if (guild.getPlayers().containsKey(username)) {
				isInGuild = true;
				if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
					player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(guild.getName(), guild.getPlayers().get(username)));
					break;
				}

				Member data = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
				if (!data.getGuildKey().equals(guild.getName())) {
					if (!guild.getRanks().contains(data.getRank())) {
						guild.demoteMember(server, username);
					} else {
						player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(guild.getName(), member.getRank()));
					}
				}
				break;
			}
		}

        state.markDirty();

		if (!isInGuild) {
			player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
		}
	}

	public static Identifier GPLoc(String path) {
		return Identifier.of(GuildedParties.MOD_ID, path);
	}
}
