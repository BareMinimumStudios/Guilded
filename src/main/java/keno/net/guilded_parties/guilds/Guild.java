package keno.net.guilded_parties.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Guild {
    private final Identifier id;
    private final Map<String, Integer> ranks;
    public static final Codec<Guild> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("guild_id").forGetter(Guild::getId),
            Codec.unboundedMap(Codec.STRING, Codec.INT).stable().fieldOf("ranks").forGetter(Guild::getRanks)
    ).apply(instance, Guild::new));

    public Guild(Identifier id, Map<String, Integer> ranks) {
        this.id = id;
        this.ranks = ranks;
    }

    public Identifier getId() {
        return this.id;
    }

    public Map<String, Integer> getRanks() {
        return this.ranks;
    }
}
