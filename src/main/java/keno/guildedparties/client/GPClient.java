package keno.guildedparties.client;

import keno.guildedparties.client.screens.GuildedMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
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
                    client.setScreen(new GuildedMenuScreen());
                }
            }
        });
    }
}
