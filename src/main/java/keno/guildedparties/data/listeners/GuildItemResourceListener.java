package keno.guildedparties.data.listeners;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.items.GuildTagList;
import keno.guildedparties.utils.Utils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuildItemResourceListener implements SimpleResourceReloadListener<Map<String, GuildTagList>> {
    @Override
    public CompletableFuture<Map<String, GuildTagList>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, GuildTagList> items = new HashMap<>();
            for (var resource : manager.findResources("guilded/guild_items", id ->
                    id.getPath().endsWith(".json")).entrySet()) {
                Identifier id = resource.getKey();

                // Essentially reducing the string into it's file name
                String name = Utils.reduceIdToFilename(id, Utils.FileType.JSON);

                try (var inputStream = resource.getValue().getInputStream()) {
                    var json = GuildedParties.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                    GuildTagList list = GuildTagList.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                    items.put(name, list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return items;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Map<String, GuildTagList> map, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.runAsync(() -> HeardData.loadGuildItems(map), executor);
    }

    @Override
    public Identifier getFabricId() {
        return GuildedParties.GPLoc("guild_items");
    }
}
