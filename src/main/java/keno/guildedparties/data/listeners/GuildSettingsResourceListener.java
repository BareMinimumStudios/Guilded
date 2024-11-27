package keno.guildedparties.data.listeners;

import com.google.gson.JsonObject;
import io.wispforest.endec.format.gson.GsonDeserializer;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.utils.IdUtils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuildSettingsResourceListener implements SimpleResourceReloadListener<Map<String, GuildSettings>> {
    @Override
    public CompletableFuture<Map<String, GuildSettings>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, GuildSettings> settingsMap = new HashMap<>();
            for (var resource : manager.findResources("guilded/guild_settings", id
                    -> id.getPath().endsWith(".json")).entrySet()) {
                Identifier id = resource.getKey();

                // Essentially reducing the string into it's file name
                String name = IdUtils.reduceIdToFilename(id, IdUtils.FileType.JSON);

                try (var inputStream = resource.getValue().getInputStream()) {
                    var json = GuildedParties.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                    GuildSettings settings = GuildSettings.endec.decodeFully(GsonDeserializer::of, json);
                    settingsMap.put(name, settings);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Map.copyOf(settingsMap);
        });
    }

    @Override
    public CompletableFuture<Void> apply(Map<String, GuildSettings> data, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(() -> HeardData.loadGuildSettings(data));
    }

    @Override
    public Identifier getFabricId() {
        return GuildedParties.GPLoc("guild_settings_resource_listener");
    }
}
