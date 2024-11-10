package keno.guildedparties.data.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Object containing a guild's settings */
public record GuildSettings(boolean isPrivate,
                            int managePlayerRankPriority,
                            int managePlayerPriority,
                            int manageGuildPriority,
                            int invitePlayersPriority) {
    public static Codec<GuildSettings> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.stable().optionalFieldOf("isPrivate", false).forGetter(GuildSettings::isPrivate),
            Codec.INT.stable().optionalFieldOf("managePlayerRanks", 5).forGetter(GuildSettings::managePlayerRankPriority),
            Codec.INT.stable().optionalFieldOf("managePlayers", 3).forGetter(GuildSettings::managePlayerPriority),
            Codec.INT.stable().optionalFieldOf("manageGuild", 3).forGetter(GuildSettings::manageGuildPriority),
            Codec.INT.stable().optionalFieldOf("invitePlayers", 5).forGetter(GuildSettings::invitePlayersPriority)
    ).apply(instance, GuildSettings::new));
}
