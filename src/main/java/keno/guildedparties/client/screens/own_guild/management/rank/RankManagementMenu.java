package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import net.minecraft.client.resource.language.I18n;

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
        flowLayout.childById(TextBoxComponent.class, "rank-name")
                .text(textBoxDefault());
    }

    public String textBoxDefault() {
        if (I18n.hasTranslation("gui.guildedparties.rank_name")) {
            return I18n.translate("gui.guildedparties.rank_name");
        }
        return "Rank name";
    }
}
