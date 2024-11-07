package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.guilds.Rank;

public record Member(String guildKey, Rank rank) {
    public static Codec<Member> codec = RecordCodecBuilder.create(instance -> instance.group(
       Codec.STRING.stable().fieldOf("guildKey").forGetter(Member::guildKey),
       Rank.codec.fieldOf("rank").forGetter(Member::rank)
    ).apply(instance, Member::new));

    @Override
    public String toString() {
        return "GuildKey: " + this.guildKey() + " Rank: " + this.rank().name();
    }
}
