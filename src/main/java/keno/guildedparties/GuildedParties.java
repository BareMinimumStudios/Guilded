package keno.guildedparties;

import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.server.commands.GPCommandRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GuildedParties implements ModInitializer {
	public static final String MOD_ID = "guildedparties";
	public static RegistryKey<Registry<Guild>> GUILD_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "guilds"));
	public static RegistryKey<Registry<GuildSettings>> SETTINGS_REGISTRY = RegistryKey.ofRegistry(Identifier.of("guilded", "settings"));

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(GUILD_REGISTRY, Guild.codec, Guild.codec);
		DynamicRegistries.registerSynced(SETTINGS_REGISTRY, GuildSettings.codec, GuildSettings.codec);
		GPAttachmentTypes.init();
		GPCommandRegistry.init();

		ServerLifecycleEvents.SERVER_STARTED.register(GuildedParties::fillPersistentState);
		ServerPlayConnectionEvents.JOIN.register(GuildedParties::syncAndInitializePlayerData);
	}

	public static void fillPersistentState(MinecraftServer server) {
		// Add any data-registered guilds to the state
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		Registry<Guild> guildRegistry = server.getRegistryManager().getOrThrow(GUILD_REGISTRY);
		Registry<GuildSettings> settingsRegistry = server.getRegistryManager().getOrThrow(SETTINGS_REGISTRY);
		guildRegistry.stream().iterator().forEachRemaining(guild -> {
			if (!state.guilds.containsKey(guild.getName())) {
				state.guilds.put(guild.getName(), guild);
			}
			if (!state.guildSettingsMap.containsKey(guild.getName())) {
				GuildSettings settings;
				if (settingsRegistry.contains(RegistryKey.of(SETTINGS_REGISTRY, GPLoc(guild.getName())))) {
					settings = settingsRegistry.getEntry(GPLoc(guild.getName())).orElseThrow().value();
				} else {
					settings = new GuildSettings(false, 5, 3, 3, 5);
				}
				state.guildSettingsMap.put(guild.getName(), settings);
			}
		});
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void syncAndInitializePlayerData(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
		// Sync guild data to player attachment
		StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
		ServerPlayerEntity player = handler.getPlayer();
		UUID playerId = player.getUuid();
		boolean isInGuild = false;
		for (Guild guild : state.guilds.values()) {
			if (state.banLists.containsKey(guild.getName())) {
				if (state.banLists.get(guild.getName()).isPlayerBanned(playerId)) continue;
			}

			if (guild.getPlayers().containsKey(playerId)) {
				isInGuild = true;
				if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
					player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(guild.getName(), guild.getPlayers().get(playerId)));
					break;
				}

				Member data = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
				if (!data.guildKey.equals(guild.getName())) {
					if (!guild.getRanks().contains(data.rank())) {
						guild.demoteMember(player);
					} else {
						player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(guild.getName(), member.rank()));
					}
				}
				break;
			}
		}
		if (!isInGuild) {
			player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
		}
	}

	public static Identifier GPLoc(String path) {
		return Identifier.of(GuildedParties.MOD_ID, path);
	}
}