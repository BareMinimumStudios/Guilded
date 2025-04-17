package keno.guildedparties.api.server.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PlayerSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        // Thankfully, the ServerCommandSource has a method to get a list of player names.
        Collection<String> playerNames = source.getPlayerNames();

        // Add all player names to the builder.
        for (String playerName : playerNames) {
            suggestPlayer(server, source, builder, playerName);
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }

    public void suggestPlayer(MinecraftServer server, ServerCommandSource source, SuggestionsBuilder builder, String username) {
        builder.suggest(username);
    }
}
