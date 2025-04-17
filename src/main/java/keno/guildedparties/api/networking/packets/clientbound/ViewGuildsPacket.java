package keno.guildedparties.api.networking.packets.clientbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import keno.guildedparties.api.data.guilds.GuildDisplayInfo;

import java.util.List;

public record ViewGuildsPacket(List<GuildDisplayInfo> infos, boolean isInGuild) {
    public static StructEndec<ViewGuildsPacket> endec = StructEndecBuilder.of(
            GuildDisplayInfo.ENDEC.listOf().fieldOf("displayInfo", ViewGuildsPacket::infos),
            StructEndec.BOOLEAN.fieldOf("isInGuild", ViewGuildsPacket::isInGuild),
            ViewGuildsPacket::new);

    public static PacketBufSerializer<ViewGuildsPacket> SERIALIZER = PacketBufSerializer.createRecordSerializer(ViewGuildsPacket.class);
}
