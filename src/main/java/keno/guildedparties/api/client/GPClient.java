package keno.guildedparties.api.client;

import keno.guildedparties.api.compat.client.GuildedClientCompatEntrypoint;
// import keno.guildedparties.api.networking.GPNetworking;
import net.fabricmc.api.ClientModInitializer;
// import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
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

        /* ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuildMenu.wasPressed()) {
                if (client.currentScreen == null) {
                    GPNetworking.GP_CHANNEL.clientHandle().send(new DoesPlayerHaveGuildPacket());
                }
            }
        }); */

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

    public static void handleClientNetworking() {

    }
}
