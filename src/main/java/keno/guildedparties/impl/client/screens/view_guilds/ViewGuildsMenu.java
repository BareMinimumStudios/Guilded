package keno.guildedparties.impl.client.screens.view_guilds;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.client.custom.abstract_screen.AbstractSingleLoadXMLScreen;

import java.util.List;
import java.util.Map;

public class ViewGuildsMenu extends AbstractSingleLoadXMLScreen<FlowLayout> {
    // These are done on screen initialization
    private final List<GuildDisplayInfo> guilds;
    private final boolean isPlayerInGuild;
    private final int guildDisplayCount;


    // These variables are initialized in beforeElementLoad
    private int guiScale;

    public ViewGuildsMenu(List<GuildDisplayInfo> guilds, boolean isPlayerInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("view_guilds_ui")));
        this.guilds = guilds;
        this.isPlayerInGuild = isPlayerInGuild;
        this.guildDisplayCount = guilds.size();
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.guiScale = this.client.options.getGuiScale().getValue();
        int generalSizing = 15 * guiScale;

        rootComponent.childById(FlowLayout.class, "main_layout").sizing(Sizing.fill(generalSizing));
    }

    @Override
    protected void beforeElementLoad(OwoUIAdapter<FlowLayout> uiAdapter) {

    }

    @Override
    protected void loadElements(OwoUIAdapter<FlowLayout> uiAdapter) {
        this.guiScale = this.client.options.getGuiScale().getValue();
        for (GuildDisplayInfo guild : guilds) {
            uiAdapter.rootComponent.childById(FlowLayout.class, "guild_list")
                    .child(getGuildElement(guild, this.guiScale));
        }
    }

    @Override
    protected void afterElementLoad(OwoUIAdapter<FlowLayout> uiAdapter) {

    }

    private Component getGuildElement(GuildDisplayInfo info, final int guiScale) {
        int guildCount = this.guildDisplayCount;
        return this.model.expandTemplate(FlowLayout.class, "guild",
                Map.of("guild_name", info.guildName,
                        "leader_name", info.leaderName,
                        "members", String.valueOf(info.members),
                        "description", info.description));
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
