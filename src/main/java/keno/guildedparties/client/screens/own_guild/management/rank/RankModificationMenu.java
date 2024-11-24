package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.packets.serverbound.ModifyRankPacket;
import keno.guildedparties.utils.MathUtil;

import java.util.List;
import java.util.Map;

public class RankModificationMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    private boolean elementsLoaded = false;

    // Selected rank and modified info
    private Rank selectedRank = null;
    private int newRankPriority;
    private String newRankName = "No rank selected";

    public RankModificationMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_modification_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(TextBoxComponent.class, "rank-name")
                .text(newRankName)
                .onChanged().subscribe(value -> this.newRankName = value);

        flowLayout.childById(DiscreteSliderComponent.class, "rank-priority")
                .value(1)
                .onChanged().subscribe(value -> this.newRankPriority = (int) value);

        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .active(false)
                .onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("modify this rank",
                        new ModifyRankPacket(this.guildName, selectedRank, new Rank(this.newRankName, this.newRankPriority)))));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!elementsLoaded) {
            int i = 0;
            for (Rank rank : this.ranks) {
                if (!rank.name().equals("Recruit")) {
                    this.uiAdapter.rootComponent.childById(FlowLayout.class, "rank-container")
                            .child(getRankElement(rank, i++));
                }
            }

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getRankElement(Rank rank, int iteration) {
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

                    this.uiAdapter.rootComponent.childById(TextBoxComponent.class, "rank-name")
                                    .text(this.selectedRank.name());

                    this.uiAdapter.rootComponent.childById(DiscreteSliderComponent.class, "rank-priority")
                                    .value(MathUtil.normalizeValues(this.selectedRank.priority(), 1, 50));

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
