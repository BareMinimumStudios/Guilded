package keno.guildedparties.api.data;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public record Rank(String name, int priority) {
    public static final StructEndec<Rank> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("rank_name", Rank::name),
            StructEndec.INT.fieldOf("rank_priority", Rank::priority),
        Rank::new);

    public boolean isCoLeader() {
        return priority <= 1;
    }
}
