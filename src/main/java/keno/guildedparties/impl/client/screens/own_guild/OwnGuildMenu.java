package keno.guildedparties.impl.client.screens.own_guild;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.client.custom.GPSurfaces;
import keno.guildedparties.impl.client.screens.ActionConfirmScreen;
import keno.guildedparties.impl.client.screens.own_guild.management.GuildManagementMenu;
import keno.guildedparties.impl.data.guilds.Rank;
import keno.guildedparties.impl.data.player.Member;
import keno.guildedparties.impl.networking.GPNetworking;
import keno.guildedparties.impl.networking.packets.serverbound.GetGuildSettingsPacket;
import keno.guildedparties.impl.networking.packets.serverbound.GetInvitablePlayersPacket;
import keno.guildedparties.impl.networking.packets.serverbound.LeaveGuildPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OwnGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private final Member member;
    private final Map<String, Rank> players;
    private final List<Rank> ranks;
    private final String summary;
    private final boolean customTextures;

    // Setup surface
    private final Surface surface;

    private boolean elementsLoaded = false;
    private final FlowLayout container = Containers
            .horizontalFlow(Sizing.content(), Sizing.content());

    private final FlowLayout playerContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());

    public OwnGuildMenu(Member member, Map<String, Rank> players,
                        List<Rank> ranks, String description,
                        boolean customTextures) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("own_guild_ui")));
        this.member = member;
        this.players = players;
        this.ranks = ranks;
        this.summary = description;

        this.customTextures = customTextures;
        Identifier textureId = this.customTextures ? GuildedParties.GPLoc(member.getGuildKey()
                .strip().toLowerCase().replace(" ", "_")): Identifier.of("");
        this.surface = customTextures ? GPSurfaces.createCustomSurface(textureId) : Surface.PANEL;
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

        if (!this.elementsLoaded) {
            this.uiAdapter.rootComponent.child(this.container
                    .surface(surface)
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                    .padding(Insets.of(3)));

            this.container.child(getGuildDescriptionElement(this.model).surface(this.customTextures ? surface : Surface.outline(Color.BLACK.argb())));

            Size size = this.container.childById(FlowLayout.class, "guild_description").fullSize();

            this.container.child(Containers.verticalScroll(Sizing.content(), Sizing.fixed(size.height()), this.playerContainer.id("player_container"))
                    .surface(this.customTextures ? Surface.BLANK : Surface.outline(Color.BLACK.argb()))
                    .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                    .padding(Insets.of(2))
                    .positioning(Positioning.layout()));

            for (String username : this.players.keySet()) {
                this.playerContainer.child(getGuildmateElement(this.model, username, this.players.get(username)));
            }

            this.elementsLoaded = true;
        }
    }

    public FlowLayout getGuildDescriptionElement(UIModel model) {
        FlowLayout layout = model.expandTemplate(FlowLayout.class, "guild-description",
                        Map.of("guild-name", this.member.getGuildKey(),
                                "your-rank", this.member.getRank().name(),
                                "description", this.summary));

        layout.childById(ScrollContainer.class, "description-container")
                .sizing(Sizing.content(), Sizing.expand(20));

        layout.childById(ButtonComponent.class, "leave-button").onPress(button
                -> this.client.setScreen(new ActionConfirmScreen<>("leave the guild",
                new LeaveGuildPacket(this.member.getGuildKey()))));

        layout.childById(ButtonComponent.class, "invite-button").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetInvitablePlayersPacket()));

        layout.childById(ButtonComponent.class, "settings-button")
                .active(this.member.getRank().isCoLeader())
                .onPress(button
                        -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetGuildSettingsPacket(this.member.getGuildKey())));

        layout.childById(ButtonComponent.class, "management-button")
                .active(this.member.getRank().isCoLeader())
                .onPress(button
                        -> this.client.setScreen(new GuildManagementMenu(this.member.getGuildKey(), this.players,
                        this.ranks, this.summary)));

        return layout;
    }

    public FlowLayout getGuildmateElement(UIModel model, String username, Rank playerRank)  {
        FlowLayout guildmateElement = model.expandTemplate(FlowLayout.class, "guildmate-element",
                Map.of("guildmate-name", username, "guildmate-rank", playerRank.name()));

        guildmateElement.childById(ButtonComponent.class, "view-guildmate-button")
                .onPress(button -> this.client.setScreen(new ViewGuildmateScreen(this.ranks,
                        this.member.getGuildKey(),
                        username, playerRank)));

        return guildmateElement;
    }
}
