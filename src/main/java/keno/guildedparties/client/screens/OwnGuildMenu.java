package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.GridLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;

import java.util.List;
import java.util.Map;

public class OwnGuildMenu extends BaseUIModelScreen<GridLayout> {
    public OwnGuildMenu(Member member, Map<String, Rank> players, List<Rank> ranks) {
        super(GridLayout.class, DataSource.asset(GuildedParties.GPLoc("own_guild_menu_ui")));

    }

    @Override
    protected void build(GridLayout gridLayout) {

    }
}
