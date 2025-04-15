package keno.guildedparties.api.data.guilds;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.Rank;
import keno.guildedparties.api.data.player.attachments.MemberComponent;
import keno.guildedparties.api.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Guild {
    public static final StructEndec<Guild> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_name", Guild::getName),
            Rank.ENDEC.mapOf().fieldOf("players", Guild::getPlayers),
            Rank.ENDEC.listOf().fieldOf("ranks", Guild::getRanks),
            StructEndec.STRING.fieldOf("description", Guild::getDescription),
    Guild::new);

    private String name;
    private final HashMap<String, Rank> players = new HashMap<>();
    private final List<Rank> ranks = new ArrayList<>();
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Rank> getPlayers() {
        return players;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rank getPlayerRank(String username) {
        return this.players.get(username);
    }

    public Rank getPlayerRank(ServerPlayerEntity player) {
        return getPlayerRank(player.getGameProfile().getName());
    }

    public Rank getRank(String name) {
        return ranks.stream().filter(rank -> rank.name().equals(name)).findFirst().get();
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
            return changeMemberRank(server, username, rank);
        }
        return 0;
    }

    public int promoteMember(MinecraftServer server, String username) {

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
            return changeMemberRank(server, username, rank);
        }
        return 0;
    }

    /** Mod developers are highly recommended to use this for changing player ranks, since it avoids the need to filter through the rank list
     * @param player The player you want to change the rank of
     * @param rank The rank you're changing the player to
     * @return 1 if successful, 0 if it fails*/
    public int changeMemberRank(ServerPlayerEntity player, Rank rank) {
        String username = player.getGameProfile().getName();

        if (!GPComponents.MEMBER_KEY.get(player).hasMemberData()) return 0;

        if (this.players.containsKey(username)) {
            this.players.put(username, rank);
            MemberComponent component = GPComponents.MEMBER_KEY.get(player);
            component.changeMemberData(new Member(this.name, rank));
            return 1;
        }
        return 0;
    }

    public int changeMemberRank(MinecraftServer server, String playerUsername, Rank rank) {
        if (this.players.containsKey(playerUsername)) {
            this.players.put(playerUsername, rank);

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUsername);
            if (player != null) {
                if (!GPComponents.MEMBER_KEY.isProvidedBy(player)) return 0;

                MemberComponent component = GPComponents.MEMBER_KEY.get(player);
                component.changeMemberData(new Member(this.name, rank));
            }
            return 1;
        }
        return 0;
    }

    public void addPlayerToGuild(ServerPlayerEntity player, String rankName) {
        if (!players.containsKey(player.getGameProfile().getName())) {
            MemberComponent component = GPComponents.MEMBER_KEY.get(player);
            if (!component.hasMemberData()) {
                Rank playerRank = ranks.stream().filter(rank -> rank.name().equals(rankName)).findFirst().get();
                players.put(player.getGameProfile().getName(), playerRank);
                component.changeMemberData(new Member(this.name, playerRank));
            }
        }
    }

    public void removePlayerFromGuild(ServerPlayerEntity player) {
        if (players.containsKey(player.getGameProfile().getName())) {
            players.remove(player.getGameProfile().getName());
            GPComponents.MEMBER_KEY.get(player).changeMemberData(null);
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
}
