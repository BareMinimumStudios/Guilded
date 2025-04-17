package keno.guildedparties.api.data;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.serialization.PacketBufSerializer;

public record Rank(String name, int priority) {
    public static final StructEndec<Rank> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("rank_name", Rank::name),
            StructEndec.INT.fieldOf("rank_priority", Rank::priority),
        Rank::new);

    public static final PacketBufSerializer<Rank> SERIALIZER = PacketBufSerializer.createRecordSerializer(Rank.class);

    public boolean isCoLeader() {
        return priority <= 1;
    }
}
