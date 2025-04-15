package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.data.guilds.GuildSettings;

public record ChangeGuildSettingsPacket(String guildName, GuildSettings settings) {
    public static StructEndec<ChangeGuildSettingsPacket> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("guildName", ChangeGuildSettingsPacket::guildName),
            GuildSettings.ENDEC.fieldOf("settings", ChangeGuildSettingsPacket::settings),
            ChangeGuildSettingsPacket::new);
}
