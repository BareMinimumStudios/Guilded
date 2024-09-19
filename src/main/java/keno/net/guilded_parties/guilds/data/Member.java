package keno.net.guilded_parties.guilds.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record Member(UUID player_id, String rank_name) {
    public static final Codec<Member> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("uuid").forGetter(Member::player_id),
            Codec.STRING.fieldOf("rank").forGetter(Member::rank_name)
    ).apply(instance, Member::new));

    public PacketByteBuf memberToPacket() {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeUuid(this.player_id());
        packet.writeString(this.rank_name);
        return packet;
    }

    public static Member packetToMember(PacketByteBuf packet) {
        UUID uuid = packet.readUuid();
        String rank_name = packet.readString();
        return new Member(uuid, rank_name);
    }
}
