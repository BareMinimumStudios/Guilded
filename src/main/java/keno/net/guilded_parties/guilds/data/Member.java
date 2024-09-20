package keno.net.guilded_parties.guilds.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record Member(UUID playerId, String rankName) {
    public static final Codec<Member> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("uuid").forGetter(Member::playerId),
            Codec.STRING.fieldOf("rank").forGetter(Member::rankName)
    ).apply(instance, Member::new));

    public PacketByteBuf memberToPacket() {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeUuid(this.playerId());
        packet.writeString(this.rankName);
        return packet;
    }

    public static Member packetToMember(PacketByteBuf packet) {
        UUID uuid = packet.readUuid();
        String rank_name = packet.readString();
        return new Member(uuid, rank_name);
    }
}
