package keno.guildedparties.impl.client.screens.view_guilds;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.client.custom.abstract_screen.AbstractSingleLoadXMLScreen;
import net.minecraft.util.math.MathHelper;

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
        int entry = 0;
        FlowLayout subsection = getListSubsection();
        for (GuildDisplayInfo guild : guilds) {
            subsection.child(getGuildElement(guild, entry++, this.guiScale));
            // Create a new subsection every third guild
            if (entry % 2 == 0) {
                // Prevent modification of sub-section after it's been passed
                final FlowLayout oldSubsection = subsection;
                uiAdapter.rootComponent.childById(FlowLayout.class, "guild_list")
                        .child(oldSubsection);
                subsection = getListSubsection();
            }
        }

        uiAdapter.rootComponent.childById(FlowLayout.class, "guild_list").padding(Insets.of(2));
    }

    @Override
    protected void afterElementLoad(OwoUIAdapter<FlowLayout> uiAdapter) {

    }

    private FlowLayout getListSubsection() {
        return this.model.expandTemplate(FlowLayout.class, "list_subsection", Map.of());
    }

    private Component getGuildElement(GuildDisplayInfo info, int entry, final int guiScale) {
        ParentComponent component = this.model.expandTemplate(FlowLayout.class, "guild",
                Map.of("guild_name", info.guildName,
                        "leader_name", info.leaderName,
                        "members", String.valueOf(info.members),
                        "description", info.description));

        component.childById(ButtonComponent.class, "view")
                .onPress(button -> this.client.setScreen(new ViewGuildMenu(info, this.isPlayerInGuild)));

        if (entry > 0) component.margins(Insets.right(entry % 2 == 0 ? 0 : 1));

        return component;
    }

    public record GuildDisplayInfo(String guildName, String leaderName, int members, String description, boolean isPrivate) {
        public static Endec<GuildDisplayInfo> endec = StructEndecBuilder.of(
                Endec.STRING.fieldOf("guild_name", GuildDisplayInfo::guildName),
                Endec.STRING.fieldOf("leader_name", GuildDisplayInfo::leaderName),
                Endec.INT.fieldOf("members", GuildDisplayInfo::members),
                Endec.STRING.fieldOf("description", GuildDisplayInfo::description),
                Endec.BOOLEAN.fieldOf("is_private", GuildDisplayInfo::isPrivate),
                GuildDisplayInfo::new);
    }
}
