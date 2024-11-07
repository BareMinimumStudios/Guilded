package keno.guildedparties.server.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class RankSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        ServerCommandSource source = commandContext.getSource();
        ServerPlayerEntity player = source.getPlayer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(source.getServer());

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            return Suggestions.empty();
        }
        Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        Guild guild = state.guilds.get(member.guildKey());
        for (Rank rank : guild.getRanks()) {
            suggestionsBuilder.suggest(rank.name());
        }

        return suggestionsBuilder.buildFuture();
    }
}
