package keno.guildedparties.client.screens.own_guild.management;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.own_guild.management.leadership.LeadershipMenu;
import keno.guildedparties.client.screens.own_guild.management.rank.RankManagementMenu;
import keno.guildedparties.data.guilds.Rank;

import java.util.List;
import java.util.Map;

public class GuildManagementMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;
    private final Map<String, Rank> players;
    private final String description;

    public GuildManagementMenu(String guildName, Map<String, Rank> players, List<Rank> ranks, String description) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guild_management_ui")));
        this.guildName = guildName;
        this.players = players;
        this.ranks = ranks;
        this.description = description;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "ranks-button").onPress(button
                        -> this.client.setScreen(new RankManagementMenu(this.guildName, this.ranks)));

        flowLayout.childById(ButtonComponent.class, "leadership-button")
                .onPress(button
                        -> this.client.setScreen(new LeadershipMenu(this.guildName, this.players)));
        flowLayout.childById(ButtonComponent.class, "description-button")
                .onPress(button
                        -> this.client.setScreen(new GuildDescriptionMenu(this.guildName, this.description)));
    }
}
