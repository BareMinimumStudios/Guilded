package keno.guildedparties.data.guilds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

/** The object that stores a guild's members, name, and ranks */
@SuppressWarnings("UnstableApiUsage")
public class Guild {
    public static final Codec<Guild> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.stable().fieldOf("guild_name").forGetter(Guild::getName),
            Codec.pair(Codec.STRING.fieldOf("username").codec(),
                    Rank.codec.fieldOf("rank").codec()).listOf().fieldOf("players").forGetter(Guild::encryptPlayerHashmap),
            Rank.codec.stable().listOf().fieldOf("ranks").forGetter(Guild::getRanks)
    ).apply(instance, Guild::new));

    public static Endec<Guild> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guild_name", Guild::getName),
            Rank.endec.mapOf().fieldOf("players", Guild::getPlayers),
            Rank.endec.listOf().fieldOf("ranks", Guild::getRanks),
            Guild::new);

    private String name;
    private HashMap<String, Rank> players = new HashMap<>();
    private final List<Rank> ranks = new ArrayList<>();

    public Guild(String name, List<Pair<String, Rank>> playerList, List<Rank> ranks) {
        this.name = name;
        for (Pair<String, Rank> pair : playerList) {
            String userName = pair.getFirst();
            Rank rank = pair.getSecond();
            this.players.put(userName, rank);
        }
        this.ranks.addAll(ranks);
        Rank recruit = new Rank("Recruit", 50);
        if (!this.ranks.contains(recruit)) {
            this.ranks.add(recruit);
        }
    }

    public Guild(String name, Map<String, Rank> playerMap, List<Rank> ranks) {
        this.name = name;
        this.ranks.addAll(ranks);
        for (String playerName : playerMap.keySet()) {
            this.players.put(playerName, playerMap.get(playerName));
        }
    }


    public List<Rank> getRanks() {
        return ranks;
    }

    public Rank getRank(String name) {
        return ranks.stream().filter(rank -> rank.name().equals(name)).findFirst().get();
    }

    public ImmutableList<Pair<String, Rank>> encryptPlayerHashmap() {
        List<Pair<String, Rank>> list = new ArrayList<>();
        for (String userName : this.players.keySet()) {
            Rank value = this.players.get(userName);
            Pair<String, Rank> pair = new Pair<>(userName, value);
            list.add(pair);
        }
        ImmutableList<Pair<String, Rank>> resultant = ImmutableList.copyOf(list);
        list.clear();
        return resultant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Rank> getPlayers() {
        return players;
    }

    public int demoteMember(ServerPlayerEntity player) {
        String username = player.getGameProfile().getName();
        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;

        if (this.players.containsKey(username)) {
            Rank originalRank = this.players.get(username);
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
            player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.getGuildKey(), rank));
            this.players.put(username, rank);
            return 1;
        }
        return 0;
    }

    public int promoteMember(ServerPlayerEntity player) {
        String username = player.getGameProfile().getName();

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;

        if (this.players.containsKey(username)) {
            Rank originalRank = this.players.get(username);
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
            player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.getGuildKey(), rank));
            this.players.put(username, rank);
            return 1;
        }
        return 0;
    }

    /** Mod developers are highly recommended to use this for changing player ranks, since it avoids the need to filter through the rank list
     * @param player The player you want to change the rank of
     * @param rank The rank you're changing the player to
     * @return 1 if successful, 0 if it fails*/
    public int changeMemberRank(ServerPlayerEntity player, Rank rank) {
        String username = player.getGameProfile().getName();

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;

        if (this.players.containsKey(username)) {
            this.players.put(username, rank);
            player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.getGuildKey(), rank));
            return 1;
        }
        return 0;
    }

    public void addPlayerToGuild(ServerPlayerEntity player, String rankName) {
        if (!players.containsKey(player.getGameProfile().getName())) {
            if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                Rank playerRank = ranks.stream().filter(rank -> rank.name().equals(rankName)).findFirst().get();
                players.put(player.getGameProfile().getName(), playerRank);
                player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(this.name, playerRank));
            }
        }
    }

    public void removePlayerFromGuild(ServerPlayerEntity player) {
        if (players.containsKey(player.getGameProfile().getName())) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                players.remove(player.getGameProfile().getName());
                player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            }
        }
    }

    public void removePlayerFromGuild(MinecraftServer server, String playerUsername) {
        if (players.containsKey(playerUsername)) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUsername);
            players.remove(playerUsername);
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
