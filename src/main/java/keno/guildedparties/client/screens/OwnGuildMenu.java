package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.serverbound.GetGuildSettingsPacket;
import keno.guildedparties.networking.packets.serverbound.GetInvitablePlayersPacket;
import keno.guildedparties.networking.packets.serverbound.LeaveGuildPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OwnGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private final Member member;
    private final Map<String, Rank> players;
    private final List<Rank> ranks;

    private boolean elementsLoaded = false;
    private final FlowLayout container = Containers
            .horizontalFlow(Sizing.content(), Sizing.content());

    private FlowLayout playerContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());

    public OwnGuildMenu(Member member, Map<String, Rank> players, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("own_guild_ui")));
        this.member = member;
        this.players = players;
        this.ranks = ranks;
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
                .positioning(Positioning.relative(100, 100)));
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

            this.container.child(getGuildDescriptionElement(this.model));

            for (String username : this.players.keySet()) {
                this.playerContainer.child(getGuildmateElement(this.model, username, this.players.get(username)));
            }

            this.container.child(getGuildSettingsElement(this.model));

            this.elementsLoaded = true;
        }
    }

    public FlowLayout getGuildDescriptionElement(UIModel model) {
        FlowLayout layout = model.expandTemplate(FlowLayout.class, "guild-description",
                        Map.of("guild-name", this.member.getGuildKey(),
                                "your-rank", this.member.getRank().name()));

        layout.childById(ButtonComponent.class, "leave-button").onPress(button
                -> this.client.setScreen(new ActionConfirmScreen<>("leave the guild",
                new LeaveGuildPacket(this.member.getGuildKey()))));

        layout.childById(ButtonComponent.class, "invite-button").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetInvitablePlayersPacket()));

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

    public FlowLayout getGuildSettingsElement(UIModel model) {
        FlowLayout guildSettingsElement = model.expandTemplate(FlowLayout.class, "guild-settings",
                Map.of());

        guildSettingsElement.childById(ButtonComponent.class, "settings-button")
                .onPress(button -> {
                   if (this.member.getRank().isCoLeader()) {
                       GPNetworking.GP_CHANNEL.clientHandle().send(new GetGuildSettingsPacket(this.member.getGuildKey()));
                   }
                });

        return guildSettingsElement;
    }
}
