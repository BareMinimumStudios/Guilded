package keno.guildedparties.client;

import keno.guildedparties.client.screens.GuildedMenuScreen;
import keno.guildedparties.client.screens.OwnGuildMenu;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.DoesPlayerHaveGuildPacket;
import keno.guildedparties.networking.packets.GuildedMenuPacket;
import keno.guildedparties.networking.packets.OwnGuildMenuPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBinding openGuildMenu = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key.guildedparties.openGuildMenu",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.guildedparties"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuildMenu.wasPressed()) {
                if (client.currentScreen == null) {
                    GPNetworking.GP_CHANNEL.clientHandle().send(new DoesPlayerHaveGuildPacket());
                }
            }
        });

        handleClientNetworking();
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
    }
}
