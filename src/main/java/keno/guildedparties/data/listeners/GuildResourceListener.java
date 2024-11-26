package keno.guildedparties.data.listeners;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.HeardData;
import keno.guildedparties.data.guilds.Guild;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuildResourceListener implements SimpleResourceReloadListener<List<Guild>> {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public CompletableFuture<List<Guild>> load(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Guild> guilds = new ArrayList<>();
            for (var resource : manager.findResources("guilded/guilds", id
                    -> id.getPath().endsWith(".json")).entrySet()) {
                try (var inputStream = resource.getValue().getInputStream()) {
                    var json = GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                    Guild guild = Guild.codec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
                    guilds.add(guild);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return List.copyOf(guilds);
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(List<Guild> data, ResourceManager manager, Executor executor) {
        return CompletableFuture.runAsync(() -> HeardData.loadGuilds(data), executor);
    }

    @Override
    public Identifier getFabricId() {
        return GuildedParties.GPLoc("guilds_resource_listener");
    }
}
