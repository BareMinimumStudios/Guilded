package keno.guildedparties.client.screens;

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
        for (Rank rank : ranks) {
            GuildedParties.LOGGER.info(rank.name());
        }
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "add-rank")
                .onPress(button -> this.client.setScreen(new RankAdditionMenu(this.guildName, this.ranks)));
    }
}
