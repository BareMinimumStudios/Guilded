package keno.guildedparties.api.data.player;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.Rank;

public class Member {
    public static final StructEndec<Member> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_key", Member::getGuildKey),
            Rank.ENDEC.fieldOf("rank", Member::getRank),
    Member::new);

    private String guildKey;
    private Rank rank;

    public Member(String guildKey, Rank rank) {
        this.guildKey = guildKey;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Guild Key: " + this.getGuildKey() + " Rank: " + this.getRank();
    }

    public String getGuildKey() {
        return guildKey;
    }

    public Rank getRank() {
        return rank;
    }

    public void setGuildKey(String guildKey) {
        this.guildKey = guildKey;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
