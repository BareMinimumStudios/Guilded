package keno.guildedparties.client;

import keno.guildedparties.client.compat.GuildedClientCompatEntrypoint;
import keno.guildedparties.client.screens.GuildedMenuScreen;
import keno.guildedparties.client.screens.own_guild.InvitablePlayersScreen;
import keno.guildedparties.client.screens.own_guild.OwnGuildMenu;
import keno.guildedparties.client.screens.own_guild.management.GuildSettingsMenu;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.clientbound.GuildSettingsMenuPacket;
import keno.guildedparties.networking.packets.clientbound.InvitePlayersMenuPacket;
import keno.guildedparties.networking.packets.serverbound.DoesPlayerHaveGuildPacket;
import keno.guildedparties.networking.packets.clientbound.GuildedMenuPacket;
import keno.guildedparties.networking.packets.clientbound.OwnGuildMenuPacket;
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
            client.setScreen(new OwnGuildMenu(packet.member(), packet.players(), packet.ranks()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(InvitePlayersMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new InvitablePlayersScreen(packet.usernames()));
        });

        GPNetworking.GP_CHANNEL.registerClientbound(GuildSettingsMenuPacket.class, (packet, access) -> {
            MinecraftClient client = access.runtime();
            client.setScreen(new GuildSettingsMenu(packet.guildName(), packet.settings()));
        });
    }
}
