package keno.net.guilded_parties.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Map;

public record Guild(Identifier id, Map<String, Integer> ranks) {
    public static final Codec<Guild> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("guild_id").forGetter(Guild::id),
            Codec.unboundedMap(Codec.STRING, Codec.INT).stable().fieldOf("ranks").forGetter(Guild::ranks)
    ).apply(instance, Guild::new));
}
