package keno.guildedparties.api.data.guilds;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.serialization.PacketBufSerializer;

public record GuildDisplayInfo(String guildName, String leaderName, int members, String description) {
    public static final StructEndec<GuildDisplayInfo> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guild_name", GuildDisplayInfo::guildName),
            StructEndec.STRING.fieldOf("leader_name", GuildDisplayInfo::leaderName),
            StructEndec.INT.fieldOf("members", GuildDisplayInfo::members),
            StructEndec.STRING.fieldOf("description", GuildDisplayInfo::description),
            GuildDisplayInfo::new);

    public static final PacketBufSerializer<GuildDisplayInfo> SERIALIZER = PacketBufSerializer.createRecordSerializer(GuildDisplayInfo.class);
}
