package keno.net.guilded_parties.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record Guild(Identifier id, Map<String, Integer> ranks, List<Pair<UUID, String>> default_members) {
    public static final Codec<Guild> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("guild_id").forGetter(Guild::id),
            Codec.unboundedMap(Codec.STRING, Codec.INT).stable().fieldOf("ranks").forGetter(Guild::ranks),
            Codec.list(Codec.pair(Uuids.CODEC.fieldOf("uuid").codec(),
                    Codec.STRING.fieldOf("rank").codec())).optionalFieldOf("default_members", new ArrayList<>())
                    .forGetter(Guild::default_members)
    ).apply(instance, Guild::new));
}
