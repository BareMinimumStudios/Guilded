package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;

import java.util.List;

public class RankManagementMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    public RankManagementMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_management_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "add-rank")
                .onPress(button -> this.client.setScreen(new RankAdditionMenu(this.guildName, this.ranks)));
        flowLayout.childById(ButtonComponent.class, "remove-rank")
                .onPress(button -> this.client.setScreen(new RankRemovalMenu(this.guildName, this.ranks)));
        flowLayout.childById(ButtonComponent.class, "modify-rank")
                .onPress(button -> this.client.setScreen(new RankModificationMenu(this.guildName, this.ranks)));
    }
}
