package keno.guildedparties.api.data.listeners;

import com.google.gson.JsonObject;
import io.wispforest.endec.format.gson.GsonDeserializer;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.data.guilds.GuildSettings;
import keno.guildedparties.api.utils.ListenerUtils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuildSettingsResourceListener implements SimpleResourceReloadListener<HashMap<String, GuildSettings>> {
    @Override
    public CompletableFuture<HashMap<String, GuildSettings>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, GuildSettings> guildSettings = new HashMap<>();
            for (var resource : manager.findResources("guilded/guild_settings", id
                    -> id.getPath().endsWith(".json")).entrySet()) {
                Identifier id = resource.getKey();

                // Essentially reducing the string into it's file name
                String name = ListenerUtils.reduceIdToFilename(id, ListenerUtils.FileType.JSON);

                try (var inputStream = resource.getValue().getInputStream()) {
                    var json = GuildedParties.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                    GuildSettings guild = GuildSettings.ENDEC.decodeFully(GsonDeserializer::of, json);
                    guildSettings.put(name, guild);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return guildSettings;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(HashMap<String, GuildSettings> guildSettings, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> HeardData.loadGuildSettings(guildSettings), executor);
    }

    @Override
    public Identifier getFabricId() {
        return GuildedParties.modLoc("settings_listener");
    }
}
