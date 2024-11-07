package keno.guildedparties.server.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class GuildmateSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();

        if (!sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return Suggestions.empty();

        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

        if (!state.guilds.containsKey(senderData.guildKey())) return Suggestions.empty();

        Guild guild = state.guilds.get(senderData.guildKey());

        for (UUID uuid : guild.players.keySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            builder.suggest(player.getGameProfile().getName());
        }

        return builder.buildFuture();
    }
}
