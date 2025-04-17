package keno.guildedparties.api.data.player;

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer;
import com.bibireden.data_attributes.endec.nbt.NbtSerializer;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import keno.guildedparties.api.data.Rank;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class Member {
    public static final StructEndec<Member> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_key", Member::getGuildKey),
            Rank.ENDEC.fieldOf("rank", Member::getRank),
    Member::new);

    public static final PacketBufSerializer<Member> SERIALIZER = new PacketBufSerializer<>(((packetByteBuf, member) -> {
        NbtCompound compound = new NbtCompound();
        compound.put("member", ENDEC.encodeFully(NbtSerializer::of, member));
        packetByteBuf.writeNbt(compound);
    }), packetByteBuf -> {
        NbtCompound compound = packetByteBuf.readNbt();
        NbtElement element = compound.get("member");
        return ENDEC.decodeFully(NbtDeserializer::of, element);
    });

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
