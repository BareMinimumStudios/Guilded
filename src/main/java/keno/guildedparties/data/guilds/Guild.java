package keno.guildedparties.data.guilds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Member;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;

import java.util.*;

public class Guild {
    public static final Codec<Guild> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.stable().fieldOf("guild_name").forGetter(Guild::getName),
            Codec.pair(Uuids.CODEC.fieldOf("uuid").codec(),
                    Rank.codec.fieldOf("rank").codec()).listOf().fieldOf("players").forGetter(Guild::encryptPlayerHashmap),
            Rank.codec.stable().listOf().fieldOf("ranks").forGetter(Guild::getRanks)
    ).apply(instance, Guild::new));

    private String name;
    public HashMap<UUID, Rank> players = new HashMap<>();
    public List<Rank> ranks = new ArrayList<>();

    public Guild(String name, List<Pair<UUID, Rank>> players_list, List<Rank> ranks) {
        this.name = name;
        for (Pair<UUID, Rank> pair : players_list) {
            UUID id = pair.getFirst();
            Rank rank = pair.getSecond();
            this.players.put(id, rank);
        }
        this.ranks.addAll(ranks);
        Rank recruit = new Rank("Recruit", 50);
        if (!this.ranks.contains(recruit)) {
            this.ranks.add(recruit);
        }
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public ImmutableList<Pair<UUID, Rank>> encryptPlayerHashmap() {
        List<Pair<UUID, Rank>> list = new ArrayList<>();
        for (UUID key : this.players.keySet()) {
            Rank value = this.players.get(key);
            Pair<UUID, Rank> pair = new Pair<>(key, value);
            list.add(pair);
        }
        ImmutableList<Pair<UUID, Rank>> resultant = ImmutableList.copyOf(list);
        list.clear();
        return resultant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("UnstableApiUsage")
    public int demoteMember(ServerPlayerEntity player) {
        UUID memberId = player.getUuid();
        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;

        if (this.players.containsKey(memberId)) {
            Rank originalRank = this.players.get(memberId);
            Rank demotionRank = null;
            for (Rank rank : this.ranks) {
                if (rank.priority() > originalRank.priority()) {
                    if (demotionRank == null || rank.priority() < demotionRank.priority()) {
                        demotionRank = rank;
                    }
                }
            }

            if (demotionRank == null) return 0;
            final Rank rank = demotionRank;
            player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.guildKey(), rank));
            this.players.put(memberId, rank);
            return 1;
        }
        return 0;
    }

    @SuppressWarnings("UnstableApiUsage")
    public void addPlayerToGuild(ServerPlayerEntity player, String rank_name) {
        if (!players.containsKey(player.getUuid())) {
            if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                Rank player_rank = ranks.stream().filter(rank -> rank.name().equals(rank_name)).findFirst().get();
                players.put(player.getUuid(), player_rank);
                player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(this.name, player_rank));
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void removePlayerFromGuild(ServerPlayerEntity player) {
        if (players.containsKey(player.getUuid())) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                players.remove(player.getUuid());
                player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            }
        }
    }

    public int addRank(Rank rank) {
        if (!this.ranks.contains(rank)) {
            this.ranks.add(rank);
            return 1;
        }
        return 0;
    }
}
