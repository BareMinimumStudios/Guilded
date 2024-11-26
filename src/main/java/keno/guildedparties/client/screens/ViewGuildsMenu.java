package keno.guildedparties.client.screens;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;

import java.util.List;

public class ViewGuildsMenu extends BaseUIModelScreen<FlowLayout> {
    private final List<GuildDisplayInfo> guilds;
    private final boolean isPlayerInGuild;

    public ViewGuildsMenu(List<GuildDisplayInfo> guilds, boolean isPlayerInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("view_guilds_ui")));
        this.guilds = guilds;
        this.isPlayerInGuild = isPlayerInGuild;
    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }

    public record GuildDisplayInfo(String guildName, String leaderName, int members) {
        public static Endec<GuildDisplayInfo> endec = StructEndecBuilder.of(
                Endec.STRING.fieldOf("guild_name", GuildDisplayInfo::guildName),
                Endec.STRING.fieldOf("leader_name", GuildDisplayInfo::leaderName),
                Endec.INT.fieldOf("members", GuildDisplayInfo::members),
                GuildDisplayInfo::new);
    }
}
