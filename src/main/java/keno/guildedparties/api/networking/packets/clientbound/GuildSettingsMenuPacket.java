package keno.guildedparties.api.networking.packets.clientbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import keno.guildedparties.api.data.guilds.GuildSettings;

public record GuildSettingsMenuPacket(String guildName, GuildSettings settings) {
    public static final StructEndec<GuildSettingsMenuPacket> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guildName", GuildSettingsMenuPacket::guildName),
            GuildSettings.ENDEC.fieldOf("settings", GuildSettingsMenuPacket::settings),
    GuildSettingsMenuPacket::new);

    public static final PacketBufSerializer<GuildSettingsMenuPacket> SERIALIZER = PacketBufSerializer.createRecordSerializer(GuildSettingsMenuPacket.class);
}
