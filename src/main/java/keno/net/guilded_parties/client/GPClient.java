package keno.net.guilded_parties.client;

import keno.net.guilded_parties.GuildedParties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GPClient implements ClientModInitializer {
    private static String KEY_CATEGORY = "category.guilded_parties";
    private static KeyBinding OPEN_GUILD_MENU = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.guilded_parties.open_guild_menu",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GUILD_MENU.wasPressed()) {
                GuildedParties.LOGGER.info("Open guild menu");
            }
        });
    }
}
