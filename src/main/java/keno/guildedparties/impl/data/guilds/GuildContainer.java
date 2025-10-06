package keno.guildedparties.impl.data.guilds;

import java.util.function.Supplier;

/**
 * Used to immutably store data regarding a guild for referencing
 */
public final class GuildContainer {
    private final Supplier<Guild> guild;
    private final Supplier<GuildSettings> settings;

    public GuildContainer(Supplier<Guild> guild, Supplier<GuildSettings> settings) {
        this.guild = guild;
        this.settings = settings;
    }

    public Guild getGuild() {
        return guild.get();
    }

    public GuildSettings getSettings() {
        return settings.get();
    }

    public static GuildContainer wrapGuildData(Guild guild, GuildSettings settings) {
        return new GuildContainer(() -> guild, () -> settings);
    }
}
