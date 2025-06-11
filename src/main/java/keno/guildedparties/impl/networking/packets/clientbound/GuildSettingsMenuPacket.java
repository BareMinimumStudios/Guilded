package keno.guildedparties.impl.networking.packets.clientbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.impl.data.guilds.GuildSettings;

public record GuildSettingsMenuPacket(String guildName, GuildSettings settings) {
    public static StructEndec<GuildSettingsMenuPacket> endec = StructEndecBuilder.of(
            Endec.STRING.fieldOf("guildName", GuildSettingsMenuPacket::guildName),
            GuildSettings.endec.fieldOf("settings", GuildSettingsMenuPacket::settings),
            GuildSettingsMenuPacket::new);
}
