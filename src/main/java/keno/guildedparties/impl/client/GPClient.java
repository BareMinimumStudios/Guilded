package keno.guildedparties.impl.client;

import keno.guildedparties.impl.client.compat.GuildedClientCompatEntrypoint;
import keno.guildedparties.impl.client.screens.GuildedMenuScreen;
import keno.guildedparties.impl.client.screens.own_guild.InvitablePlayersScreen;
import keno.guildedparties.impl.client.screens.own_guild.OwnGuildMenu;
import keno.guildedparties.impl.client.screens.own_guild.management.GuildSettingsMenu;
import keno.guildedparties.impl.client.screens.view_guilds.ViewGuildsMenu;
import keno.guildedparties.impl.networking.GPNetworking;
import keno.guildedparties.impl.networking.packets.clientbound.*;
import keno.guildedparties.impl.networking.packets.serverbound.DoesPlayerHaveGuildPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;

//TODO swap UI positioning to layout for scaling fix
public class GPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBinding openGuildMenu = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.guildedparties.openGuildMenu",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.guildedparties"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuildMenu.wasPressed()) {
                if (client.currentScreen == null) {
                    GPNetworking.GP_CHANNEL.clientHandle().send(new DoesPlayerHaveGuildPacket());
                }
            }
        });

        handleClientNetworking();
        initializeCompatHelpers();
    }

    public void initializeCompatHelpers() {
        List<EntrypointContainer<GuildedClientCompatEntrypoint>> containers
                = FabricLoader.getInstance().getEntrypointContainers("guilded_client", GuildedClientCompatEntrypoint.class);

        for (EntrypointContainer<GuildedClientCompatEntrypoint> container : containers) {
            GuildedClientCompatEntrypoint entrypoint = container.getEntrypoint();
            entrypoint.initClient();
        }
    }

    /** Client-bound packets are handled here */
    private static void handleClientNetworking() {
        GPNetworking.GP_CHANNEL.registerClientbound(GuildedMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new GuildedMenuScreen(packet.isInGuild()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(OwnGuildMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new OwnGuildMenu(packet.member(), packet.players(), packet.ranks(), packet.summary(), packet.hasCustomTextures()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(InvitePlayersMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new InvitablePlayersScreen(packet.usernames()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(GuildSettingsMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new GuildSettingsMenu(packet.guildName(), packet.settings()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(ViewGuildsPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new ViewGuildsMenu(packet.infos(), packet.isInGuild()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(KickedFromMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(null);
        });
    }
}
