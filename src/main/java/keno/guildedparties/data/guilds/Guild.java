package keno.guildedparties.data.guilds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.clientbound.KickedFromMenuPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The object that stores a guild's members, name, and ranks */
@SuppressWarnings("UnstableApiUsage")
public class Guild {
    public static final Codec<Guild> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.stable().fieldOf("guild_name").forGetter(Guild::getName),
            Codec.pair(Codec.STRING.fieldOf("username").codec(),
                    Rank.codec.fieldOf("rank").codec()).listOf().fieldOf("players").forGetter(Guild::encryptPlayerHashmap),
            Rank.codec.stable().listOf().fieldOf("ranks").forGetter(Guild::getRanks),
            Codec.STRING.optionalFieldOf("description", "none").forGetter(Guild::getDescription)
    ).apply(instance, Guild::new));

    public static Endec<Guild> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guild_name", Guild::getName),
            Rank.endec.mapOf().fieldOf("players", Guild::getPlayers),
            Rank.endec.listOf().fieldOf("ranks", Guild::getRanks),
            Endec.STRING.fieldOf("description", Guild::getDescription),
            Guild::new);

    private String name;
    private HashMap<String, Rank> players = new HashMap<>();
    private final List<Rank> ranks = new ArrayList<>();
    private String description;

    public Guild(String name, List<Pair<String, Rank>> playerList, List<Rank> ranks, String description) {
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
        this.description = description;
    }

    public Guild(String name, Map<String, Rank> playerMap, List<Rank> ranks, String description) {
        this.name = name;
        Rank recruit = new Rank("Recruit", 50);
        if (!ranks.contains(recruit)) {
            this.ranks.add(recruit);
        }
        this.ranks.addAll(ranks);
        for (String playerName : playerMap.keySet()) {
            this.players.put(playerName, playerMap.get(playerName));
        }
        this.description = description;
    }

    public Rank getPlayerRank(String username) {
        return this.players.get(username);
    }

    public Rank getPlayerRank(ServerPlayerEntity player) {
        return getPlayerRank(player.getGameProfile().getName());
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

    public int demoteMember(MinecraftServer server, String username) {
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
            this.changeMemberRank(server, username, rank);
            return 1;
        }
        return 0;
    }

    @Deprecated
    public int demoteMember(ServerPlayerEntity player) {
        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) return 0;
        Rank originalRank = this.players.get(player.getGameProfile().getName());
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
        this.players.put(player.getGameProfile().getName(), rank);
        player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.getGuildKey(), rank));
        return 1;
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

    public int changeMemberRank(MinecraftServer server, String playerUsername, Rank rank) {
        if (this.players.containsKey(playerUsername)) {
            this.players.put(playerUsername, rank);

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUsername);
            if (player != null) {
                player.modifyAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, member -> new Member(member.getGuildKey(), rank));
            }
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
                    GPNetworking.GP_CHANNEL.serverHandle(player).send(new KickedFromMenuPacket());
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

    public boolean isPlayerInGuild(String username) {
        return this.players.containsKey(username);
    }

    public boolean isPlayerInGuild(ServerPlayerEntity player) {
        return isPlayerInGuild(player.getGameProfile().getName());
    }

    public int addRank(Rank rank) {
        if (this.ranks.stream().noneMatch(currentRank -> currentRank.name().equals(rank.name()))) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
