package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OwnGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private final Member member;
    private final Map<String, Rank> players;

    private boolean elementsLoaded = false;
    private final FlowLayout container = Containers
            .horizontalFlow(Sizing.content(), Sizing.content());

    private FlowLayout playerContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());

    public OwnGuildMenu(Member member, Map<String, Rank> players, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("own_guild_ui")));
        this.member = member;
        this.players = players;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .sizing(Sizing.fill(100));

        this.container.child(Containers.verticalScroll(Sizing.fill(50), Sizing.fill(50), this.playerContainer)
                .surface(Surface.VANILLA_TRANSLUCENT).alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                .padding(Insets.of(1)).positioning(Positioning.relative(99, 97)));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            this.uiAdapter.rootComponent.child(this.container
                    .surface(Surface.DARK_PANEL)
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                    .sizing(Sizing.fill(90))
                    .margins(Insets.of(10)));

            for (String username : this.players.keySet()) {
                String playerRank = this.players.get(username).name();
                this.playerContainer = this.playerContainer
                        .child(this.model.expandTemplate(FlowLayout.class, "guildmate-element",
                                Map.of("guildmate-name", username, "guildmate-rank", playerRank)));
            }

            this.elementsLoaded = true;
        }
    }
}
