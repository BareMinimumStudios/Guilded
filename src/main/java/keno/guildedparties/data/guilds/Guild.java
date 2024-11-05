package keno.guildedparties.data.guilds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Guild {
    public static final Codec<Guild> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.stable().fieldOf("guild_name").forGetter(Guild::getName),
            Codec.pair(Uuids.CODEC, Rank.codec).listOf().fieldOf("players").forGetter(Guild::encryptPlayerHashmap),
            Rank.codec.stable().listOf().fieldOf("ranks").forGetter(Guild::getRanks)
    ).apply(instance, Guild::new));

    public String name;
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
}
