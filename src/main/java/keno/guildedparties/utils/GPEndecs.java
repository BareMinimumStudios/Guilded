package keno.guildedparties.utils;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;

public class GPEndecs {
    public static Endec<Member> MEMBER = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guildKey", Member::getGuildKey),
            Rank.endec.fieldOf("rank", Member::getRank),
            Member::new);

    public static void registerEndecs() {

    }
}
