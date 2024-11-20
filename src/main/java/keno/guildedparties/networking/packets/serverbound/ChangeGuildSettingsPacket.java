package keno.guildedparties.networking.packets.serverbound;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.data.guilds.GuildSettings;

public record ChangeGuildSettingsPacket(String guildName, GuildSettings settings) {
    public static Endec<ChangeGuildSettingsPacket> endec = StructEndecBuilder.of(
        Endec.STRING.fieldOf("guildName", ChangeGuildSettingsPacket::guildName),
        GuildSettings.endec.fieldOf("settings", ChangeGuildSettingsPacket::settings),
        ChangeGuildSettingsPacket::new);
}
