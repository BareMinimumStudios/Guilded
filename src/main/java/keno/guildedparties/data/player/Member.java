package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.guilds.Rank;

/** Data stored on players to get their guildKey and rank, without checking guilds themselves */
public class Member {
    /** The guild's name, used for data retrieval and verification of guild membership */
    private String guildKey;
    /** The player's rank in a guild*/
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

    /** @see Member#guildKey */
    public String guildKey() {
        return guildKey;
    }

    /** @see Member#rank */
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
