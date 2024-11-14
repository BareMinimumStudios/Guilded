package keno.guildedparties.data.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;

/** Rank object, stores it's name and priority (Priority is handled as: 1- = highest perms, 50 = lowest)
 * @see keno.guildedparties.data.player.Member Member
 * @see Guild */
public record Rank(String name, int priority) {
    public static final Codec<Rank> codec = RecordCodecBuilder.create(instance -> instance.group(
       Codec.STRING.stable().fieldOf("rank_name").forGetter(Rank::name),
       Codec.INT.stable().fieldOf("rank_priority").forGetter(Rank::priority)
    ).apply(instance, Rank::new));

    public static Endec<Rank> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("rank_name", Rank::name),
            Endec.INT.fieldOf("rank_priority", Rank::priority),
            Rank::new);

    public boolean isCoLeader() {
        return priority <= 1;
    }
}
