package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ViewGuildmateScreen extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final String username;
    private final Rank rank;

    private boolean elementsLoaded = false;
    private FlowLayout container = Containers.horizontalFlow(Sizing.content(), Sizing.content());

    public ViewGuildmateScreen(String guildName, String username, Rank rank) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("view_guildmate_ui")));
        this.guildName = guildName;
        this.username = username;
        this.rank = rank;
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
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!elementsLoaded) {
            this.uiAdapter.rootComponent.child(this.container
                    .surface(Surface.DARK_PANEL)
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                    .sizing(Sizing.content())
                    .margins(Insets.of(10)));

            this.container.child(getViewGuildmateElement());

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getViewGuildmateElement() {
        FlowLayout layout = this.model.expandTemplate(FlowLayout.class, "view-guildmate@guildedparties:view_guildmate_ui",
                        Map.of("username", this.username,
                                "rank", this.rank.name(),
                                "priority", String.valueOf(this.rank.priority())));
        layout.forEachDescendant(component -> {
            if (component instanceof LabelComponent) {
                ((LabelComponent)component).color(Color.BLACK);
            }
        });
        return layout;
    }
}
