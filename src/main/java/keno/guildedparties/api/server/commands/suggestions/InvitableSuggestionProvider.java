package keno.guildedparties.api.server.commands.suggestions;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import keno.guildedparties.api.networking.GPNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class InvitableSuggestionProvider extends PlayerSuggestionProvider {
    @Override
    public void suggestPlayer(MinecraftServer server, ServerCommandSource source, SuggestionsBuilder builder, String username) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(username);
        if (player != null) {
            if (!GPNetworking.doesPlayerHaveMemberData(player)) {
                builder.suggest(username);
            }
        }
    }
}
