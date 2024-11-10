package keno.guildedparties.data.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;

import java.util.UUID;

/** Record for invite, uses the sender's UUID as a security*/
public record Invite(String guildName, UUID inviteSender) {
    public static Codec<Invite> codec = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.stable().fieldOf("guildName").forGetter(Invite::guildName),
        Uuids.CODEC.stable().fieldOf("inviteSender").forGetter(Invite::inviteSender)
    ).apply(instance, Invite::new));
}
