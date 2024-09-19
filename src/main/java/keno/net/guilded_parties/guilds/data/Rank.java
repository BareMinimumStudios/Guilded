package keno.net.guilded_parties.guilds.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public record Rank(String name, Integer priority) {
    public static final Codec<Rank> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.stable().fieldOf("rank_name").forGetter(Rank::name),
        Codec.INT.stable().fieldOf("rank_priority").forGetter(Rank::priority)
    ).apply(instance, Rank::new));

    public PacketByteBuf rankToPacket() {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeString(this.name);
        packet.writeInt(this.priority);
        return packet;
    }

    public static Rank packetToRank(PacketByteBuf packet) {
        String rank_name = packet.readString();
        int rank_priority = packet.readInt();
        return new Rank(rank_name, rank_priority);
    }
}
