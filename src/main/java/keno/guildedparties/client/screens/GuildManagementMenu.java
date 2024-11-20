package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;

import java.util.List;

public class GuildManagementMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    public GuildManagementMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guild_management_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "ranks-button")
                .onPress(button -> GuildedParties.LOGGER.info("Ranks button pressed"));

        flowLayout.childById(ButtonComponent.class, "leadership-button")
                .onPress(button -> GuildedParties.LOGGER.info("Leadership button pressed"));
    }
}
