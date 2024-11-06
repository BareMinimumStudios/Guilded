package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record Member(String guild_key, Rank rank) {
    public static Codec<Member> codec = RecordCodecBuilder.create(instance -> instance.group(
       Codec.STRING.stable().fieldOf("guild_key").forGetter(Member::guild_key),
       Rank.codec.fieldOf("rank").forGetter(Member::rank)
    ).apply(instance, Member::new));
}
