package keno.guildedparties.client.screens.view_guilds;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.GuildedMenuScreen;

import java.util.List;
import java.util.Map;

public class ViewGuildsMenu extends BaseUIModelScreen<FlowLayout> {
    private final List<GuildDisplayInfo> guilds;
    private final boolean isPlayerInGuild;

    private boolean elementsLoaded = false;

    public ViewGuildsMenu(List<GuildDisplayInfo> guilds, boolean isPlayerInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("view_guilds_ui")));
        this.guilds = guilds;
        this.isPlayerInGuild = isPlayerInGuild;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "back")
                .onPress(button -> this.client.setScreen(new GuildedMenuScreen(isPlayerInGuild)));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!elementsLoaded) {
            for (GuildDisplayInfo info : this.guilds) {
                this.uiAdapter.rootComponent.childById(FlowLayout.class, "guild-container")
                        .child(getElement(info));
            }

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getElement(GuildDisplayInfo info) {
        FlowLayout layout = this.model.expandTemplate(FlowLayout.class, "guild-info",
                Map.of("guild-name", info.guildName,
                        "leader-name", info.leaderName,
                        "number-of-members", String.valueOf(info.members)));

        layout.childById(ButtonComponent.class, "view-button")
                .onPress(button -> this.client.setScreen(new ViewGuildMenu(info.guildName(), info.leaderName(), info.members(),
                        info.description(), this.isPlayerInGuild, false)));

        return layout;
    }

    public record GuildDisplayInfo(String guildName, String leaderName, int members, String description) {
        public static Endec<GuildDisplayInfo> endec = StructEndecBuilder.of(
                Endec.STRING.fieldOf("guild_name", GuildDisplayInfo::guildName),
                Endec.STRING.fieldOf("leader_name", GuildDisplayInfo::leaderName),
                Endec.INT.fieldOf("members", GuildDisplayInfo::members),
                Endec.STRING.fieldOf("description", GuildDisplayInfo::description),
                GuildDisplayInfo::new);
    }
}
