package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
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

    private boolean templatesLoaded = false;
    private final FlowLayout container = Containers
            .horizontalFlow(Sizing.content(), Sizing.content());

    private final FlowLayout playerContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());

    private final ScrollContainer<ParentComponent> scrollContainer = Containers.verticalScroll(Sizing.content(), Sizing.fill(100),
            playerContainer.surface(Surface.DARK_PANEL));

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
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        flowLayout.child(this.container
                .surface(Surface.DARK_PANEL)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .margins(Insets.of(10)));

        this.container.child(this.scrollContainer
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.templatesLoaded) {
            this.container.child(this.model.expandTemplate(FlowLayout.class, "guild-description@guildedparties:own_guild_ui",
                            Map.of("guild-name", member.getGuildKey(), "your-rank", member.getRank().name())));

            for (String username : this.players.keySet()) {
                this.playerContainer.child(this.model.expandTemplate(FlowLayout.class, "guildmate-element@guildedparties:own_guild_ui",
                        Map.of("guildmate-name", username, "guildmate-rank", this.players.get(username).name())));
            }

            this.templatesLoaded = true;
        }
    }
}
