package keno.guildedparties.data.guilds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
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

    public Rank getRank(String name) {
        return ranks.stream().filter(rank -> rank.name().equals(name)).findFirst().get();
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

    public int promoteMember(ServerPlayerEntity player) {
        UUID memberId = player.getUuid();

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;

        if (this.players.containsKey(memberId)) {
            Rank originalRank = this.players.get(memberId);
            Rank promotionRank = null;
            for (Rank rank : this.ranks) {
                if (rank.priority() < originalRank.priority()) {
                    if (promotionRank == null || rank.priority() > promotionRank.priority()) {
                        promotionRank = rank;
                    }
                }
            }

            if (promotionRank == null) return 0;
            final Rank rank = promotionRank;
            player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.guildKey(), rank));
            this.players.put(memberId, rank);
            return 1;
        }
        return 0;
    }

    public void addPlayerToGuild(ServerPlayerEntity player, String rankName) {
        if (!players.containsKey(player.getUuid())) {
            if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                Rank playerRank = ranks.stream().filter(rank -> rank.name().equals(rankName)).findFirst().get();
                players.put(player.getUuid(), playerRank);
                player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(this.name, playerRank));
            }
        }
    }

    public void removePlayerFromGuild(ServerPlayerEntity player) {
        if (players.containsKey(player.getUuid())) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                players.remove(player.getUuid());
                player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            }
        }
    }

    public void removePlayerFromGuild(MinecraftServer server, UUID playerId) {
        if (players.containsKey(playerId)) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            players.remove(playerId);
            if (player != null) {
                if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                }
            }
        }
    }

    private void sortRanks() {
        // To ensure ranks are ordered correctly, this is to be executed whenever a rank is added or removed
        // Uses pseudocode for the insertion sort, since we aren't working with massive amounts of data.
        // We do this so finding a guild's ranks later is quicker, since we use a list to store them
        for (int i = 1; i < this.ranks.size(); i++) {
            Rank rank = ranks.get(i);
            int key = rank.priority();
            int j = i - 1;
            while (j >= 0 && this.ranks.get(j).priority() > key) {
                this.ranks.set(j + 1, this.ranks.get(j));
                j = j - 1;
            }
            this.ranks.set(j + 1, rank);
        }
    }

    public int addRank(Rank rank) {
        if (!this.ranks.contains(rank)) {
            this.ranks.add(rank);
            sortRanks();
            return 1;
        }
        return 0;
    }

    public int removeRank(String rankName) {
        for (Rank rank : getRanks()) {
            if (rank.name().equals(rankName)) {
                this.ranks.remove(rank);
                sortRanks();
                return 1;
            }
        }
        return 0;
    }
}
