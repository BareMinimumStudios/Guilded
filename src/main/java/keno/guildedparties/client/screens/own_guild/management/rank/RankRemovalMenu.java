package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.packets.serverbound.RemoveRankPacket;

import java.util.List;
import java.util.Map;

public class RankRemovalMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    private boolean elementsLoaded = false;

    private Rank selectedRank = null;

    public RankRemovalMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_removal_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .active(false).onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("remove this rank",
                        new RemoveRankPacket(guildName, selectedRank))));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            int i = 0;
            for (Rank rank : this.ranks) {
                if (!rank.name().equals("Recruit")) {
                    this.uiAdapter.rootComponent.childById(FlowLayout.class, "rank-list")
                            .child(getRankElement(rank, i++));
                }
            }

            this.elementsLoaded = true;
        }
    }

    public FlowLayout getRankElement(Rank rank, int iteration) {
        FlowLayout element = this.model.expandTemplate(FlowLayout.class, "rank-element@guildedparties:rank_selection_ui",
                Map.of("name", rank.name(), "priority", String.valueOf(rank.priority())));

        element.childById(ButtonComponent.class, "select-rank")
                .onPress(button -> {
                    this.selectedRank = rank;
                    this.uiAdapter.rootComponent.forEachDescendant(component -> {
                        if (component instanceof ButtonComponent buttonComponent) {
                            buttonComponent.active(true);
                        }
                    });
                    button.active(false);
                });

        if (iteration == 0) {
            element.margins(Insets.bottom(2));
        } else if (iteration == this.ranks.size() - 1) {
            element.margins(Insets.top(2));
        } else {
            element.margins(Insets.vertical(2));
        }

        return element;
    }
}
