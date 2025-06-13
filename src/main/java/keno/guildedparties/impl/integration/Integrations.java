package keno.guildedparties.impl.integration;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.GuildPlayerAPI;
import keno.guildedparties.impl.data.guilds.Rank;
import keno.guildedparties.impl.data.player.Member;
import keno.guildedparties.impl.utils.IntegrationUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Integrations that aren't handled via entrypoints are handled here instead.
 * Some integrations may not be safe to handle, which is why {@link IntegrationUtils#safeInitializeCompat}
 * is used.
 * <p>
 * List of integrations handled here include:
 * <br> Placeholder-API
 */
public class Integrations {
    public static void initializeIntegrations() {

    }

    /**
     * List of placeholders (All are player-dependant):
     * <p> "%guilded:guild%" - The player's guild.
     * <p> "%guilded:rank%" - The player's rank (Invalid if not in a guild).
     * <p> "%guilded:priority%" - The player's rank priority (If the player is leader, display as "leader" instead)
     * (Invalid if not in a guild).
     */
    public static void placeholdersIntegration() {
        GuildedParties.LOGGER.info("Integrating into placeholders-api");

        Placeholders.register(Identifier.of("guilded", "guild"), (ctx, arg) -> {
            if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player found!");

            ServerPlayerEntity player = ctx.player();
            Optional<Member> optional = GuildPlayerAPI.getPlayerData(player);
            if (optional.isEmpty()) {
                return PlaceholderResult.value("No-Guild");
            }

            String guildName = optional.get().getGuildKey();
            return PlaceholderResult.value(guildName);
        });

        Placeholders.register(Identifier.of("guilded", "rank"), (ctx, arg) -> {
            if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player found!");

            ServerPlayerEntity player = ctx.player();
            Optional<Member> optional = GuildPlayerAPI.getPlayerData(player);
            if (optional.isEmpty()) return PlaceholderResult.invalid("No guild found!");

            String rank = optional.get().getRank().name();
            return PlaceholderResult.value(rank);
        });

        Placeholders.register(Identifier.of("guilded", "priority"), (ctx, arg) -> {
            if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player found!");

            ServerPlayerEntity player = ctx.player();
            Optional<Member> optional = GuildPlayerAPI.getPlayerData(player);
            if (optional.isEmpty()) return PlaceholderResult.invalid("No guild found!");

            Rank rank = optional.get().getRank();
            if (rank.isCoLeader()) return PlaceholderResult.value("Leader");
            return PlaceholderResult.value(String.valueOf(rank.priority()));
        });
    }

    private static void lazyHandleIntegration(String modId, String integrationQualifiedName) {
        IntegrationUtils.safeInitializeCompat(modId, integrationQualifiedName);
    }
}
