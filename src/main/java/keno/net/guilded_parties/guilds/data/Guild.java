package keno.net.guilded_parties.guilds.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.net.guilded_parties.GuildedParties;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record Guild(Identifier id, List<Rank> ranks, List<Member> members) {
    private static final MapCodec<List<Rank>> RANKS = Rank.CODEC.listOf().fieldOf("ranks");

    private static final MapCodec<List<Member>> DEFAULT_MEMBERS = Member.CODEC.listOf().optionalFieldOf("members", new ArrayList<>());

    public static final Codec<Guild> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("guild_id").forGetter(Guild::id),
            RANKS.forGetter(Guild::ranks),
            DEFAULT_MEMBERS.forGetter(Guild::members)
    ).apply(instance, Guild::new));

    public boolean isPlayerInGuild(UUID playerUUID) {
        return this.members.stream().anyMatch(member -> member.player_id().compareTo(playerUUID) == 0);
    }

    public boolean isPlayerInGuild(ServerPlayerEntity player) {
        return this.isPlayerInGuild(player.getUuid());
    }

    public boolean addRank(Rank rank) {
        Stream<Rank> stream = this.ranks.stream();
        if (stream.noneMatch(stream_rank -> stream_rank.name().equals(rank.name()))) {
            return this.ranks.add(rank);
        }
        return false;
    }

    public boolean addPlayerToGuild(ServerPlayerEntity player, String name) {
        if (!isPlayerInGuild(player)) {
            this.members.add(new Member(player.getUuid(), name));
            return true;
        }
        return false;
    }

    public String getPlayerRank(UUID uuid) {
        Stream<Member> member_stream = this.members.stream();
        Member player_data = member_stream.filter(member -> member.player_id().compareTo(uuid) == 0).findFirst().orElseThrow();
        return player_data.rank_name();
    }

    public boolean isLeader(UUID uuid) {
        String player_rank = this.getPlayerRank(uuid);
        if (!player_rank.isBlank()) {
            Rank matching_rank = this.ranks.stream().filter(rank -> rank.name().compareTo(player_rank) == 0).findFirst().orElseThrow();
            return matching_rank.priority() == 0;
        }
        return false;
    }

    public boolean isLeader(ServerPlayerEntity player) {
        if (this.isPlayerInGuild(player)) {
            return isLeader(player.getUuid());
        }
        return false;
    }

    public PacketByteBuf guildToPacket() {
        PacketByteBuf packet = PacketByteBufs.create();
        DataResult<NbtElement> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        packet.writeNbt(result.resultOrPartial(GuildedParties.LOGGER::error).orElseThrow());
        return packet;
    }

    public static Guild packetToGuild(PacketByteBuf packet) {
        NbtElement element = packet.readNbt();
        DataResult<Guild> result = Guild.CODEC.parse(NbtOps.INSTANCE, element);
        return result.resultOrPartial(GuildedParties.LOGGER::error).orElseThrow();
    }
}
