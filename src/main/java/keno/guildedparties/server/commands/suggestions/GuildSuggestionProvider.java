package keno.guildedparties.server.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class GuildSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder builder) throws CommandSyntaxException {
        MinecraftServer server = commandContext.getSource().getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (state.getGuilds().isEmpty()) {
            return Suggestions.empty();
        }
        
        for (String guild_name : state.getGuilds().keySet()) {
            builder.suggest(guild_name);
        }
        return builder.buildFuture();
    }
}
