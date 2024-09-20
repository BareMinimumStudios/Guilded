package keno.net.guilded_parties.client;

import keno.net.guilded_parties.utils.IdUtils;
import keno.net.guilded_parties.utils.PacketIds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class GPClient implements ClientModInitializer {
    private static final String KEY_CATEGORY = "category.guilded_parties";
    private static KeyBinding OPEN_GUILD_MENU = KeyBindingHelper
            .registerKeyBinding(new KeyBinding("key.guilded_parties.open_guild_menu",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GUILD_MENU.wasPressed()) {
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeIdentifier(IdUtils.modLoc("test"));
                ClientPlayNetworking.send(PacketIds.VIEW_GUILDS, packet);
            }
        });
    }
}
