package keno.guildedparties.data.listeners;


import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.utils.Utils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
//? if < 1.21.3 {
 /*import net.minecraft.util.profiler.Profiler;
*///?}
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuildResourceListener implements SimpleResourceReloadListener<Map<String, Guild>> {
    public CompletableFuture<Map<String, Guild>> loadData(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, Guild> guilds = new HashMap<>();
            for (var resource : manager.findResources("guilded/guilds", id
                    -> id.getPath().endsWith(".json")).entrySet()) {
                Identifier id = resource.getKey();

                // Essentially reducing the string into it's file name
                String name = Utils.reduceIdToFilename(id, Utils.FileType.JSON);

                try (var inputStream = resource.getValue().getInputStream()) {
                    var json = GuildedParties.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                    Guild guild = Guild.codec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
                    guilds.put(name, guild);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return Map.copyOf(guilds);
        }, executor);
    }

    public CompletableFuture<Void> applyData(Map<String, Guild> data, Executor executor) {
        return CompletableFuture.runAsync(() -> HeardData.loadGuilds(data), executor);
    }

    //? if >=1.21.3 {
    @Override
    public CompletableFuture<Map<String, Guild>> load(ResourceManager manager, Executor executor) {
        return loadData(manager, executor);
    }
    //?}

    //? if >=1.21.3 {
    @Override
    public CompletableFuture<Void> apply(Map<String, Guild> data, ResourceManager manager, Executor executor) {
        return applyData(data, executor);
    }
    //?}

    @Override
    public Identifier getFabricId() {
        return GuildedParties.GPLoc("guilds_resource_listener");
    }

    //? if <1.21.3 {
    /*@Override
    public CompletableFuture<Map<String, Guild>> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return loadData(resourceManager, executor);
    }
    *///?}

    //? if <1.21.3 {
    /*@Override
    public CompletableFuture<Void> apply(Map<String, Guild> stringGuildMap, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return applyData(stringGuildMap, executor);
    }
     *///?}
}