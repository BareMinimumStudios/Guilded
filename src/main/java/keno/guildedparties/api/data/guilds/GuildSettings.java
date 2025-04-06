package keno.guildedparties.api.data.guilds;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record GuildSettings(boolean isPrivate,
                            int managePlayerRankPriority,
                            int managePlayerPriority,
                            int manageGuildPriority,
                            int invitePlayersPriority,
                            boolean hasCustomTextures) {
    public static StructEndec<GuildSettings> ENDEC = StructEndecBuilder.of(
            StructEndec.BOOLEAN.optionalFieldOf("isPrivate", GuildSettings::isPrivate, false),
            StructEndec.INT.optionalFieldOf("managePlayerRanks", GuildSettings::managePlayerRankPriority, 5),
            StructEndec.INT.optionalFieldOf("managePlayers", GuildSettings::managePlayerPriority, 3),
            StructEndec.INT.optionalFieldOf("manageGuild", GuildSettings::manageGuildPriority, 3),
            StructEndec.INT.optionalFieldOf("invitePlayers", GuildSettings::invitePlayersPriority, 5),
            StructEndec.BOOLEAN.optionalFieldOf("hasCustomTextures", GuildSettings::hasCustomTextures, false),
            GuildSettings::new);

    public static GuildSettings getDefaultSettings() {
        return new GuildSettings(false, 5, 3, 3, 5, false);
    }
}
