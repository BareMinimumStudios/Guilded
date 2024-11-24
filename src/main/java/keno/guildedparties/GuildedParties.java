package keno.guildedparties;

import keno.guildedparties.compat.GuildedCompatEntrypoint;
import keno.guildedparties.config.GPConfig;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildBanList;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.server.commands.GPCommandRegistry;
import keno.guildedparties.utils.GPEndecs;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@SuppressWarnings("UnstableApiUsage")
public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guildedparties";
	public static RegistryKey<Registry<Guild>> GUILD_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "guilds"));
	public static RegistryKey<Registry<GuildSettings>> SETTINGS_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "settings"));

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final GPConfig CONFIG = GPConfig.createAndLoad();

	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(GUILD_REGISTRY, Guild.codec, Guild.codec);
		DynamicRegistries.registerSynced(SETTINGS_REGISTRY, GuildSettings.codec, GuildSettings.codec);
		GPAttachmentTypes.init();
		GPCommandRegistry.init(false);

		GPEndecs.registerEndecs();
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
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		Registry<Guild> guildRegistry = server.getRegistryManager().getOrThrow(GUILD_REGISTRY);
		Registry<GuildSettings> settingsRegistry = server.getRegistryManager().getOrThrow(SETTINGS_REGISTRY);
		guildRegistry.stream().iterator().forEachRemaining(guild -> {
			if (!state.hasGuild(guild.getName())) {
				if (!guild.getName().contains(Character.toString(','))) {
					state.addGuild(guild);
				} else {
					GuildedParties.LOGGER.info("Could not add {} due to containing an illegal character", guild.getName());
					return;
				}
			}
			if (!state.doesGuildHaveSettings(guild.getName())) {
				GuildSettings settings;
				if (settingsRegistry.contains(RegistryKey.of(SETTINGS_REGISTRY, GPLoc(guild.getName())))) {
					// Due to limitations, all guild-settings need to be registered under the guildedparties namespace
					settings = settingsRegistry.getEntry(GPLoc(guild.getName())).orElseThrow().value();
				} else {
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