package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.guilds.Rank;

public class Member {
    private String guildKey;
    private Rank rank;

    public static Codec<Member> codec = RecordCodecBuilder.create(instance -> instance.group(
       Codec.STRING.stable().fieldOf("guildKey").forGetter(Member::guildKey),
       Rank.codec.stable().fieldOf("rank").forGetter(Member::rank)
    ).apply(instance, Member::new));

    public Member(String guildKey, Rank rank) {
        this.guildKey = guildKey;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "GuildKey: " + this.guildKey() + " Rank: " + this.rank().name();
    }

    public String guildKey() {
        return guildKey;
    }

    public Rank rank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setGuildKey(String guildKey) {
        this.guildKey = guildKey;
    }
}
