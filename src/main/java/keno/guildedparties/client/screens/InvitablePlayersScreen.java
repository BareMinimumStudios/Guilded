package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.serverbound.InvitePlayerPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class InvitablePlayersScreen extends BaseUIModelScreen<FlowLayout> {
    private final List<String> players;

    private boolean elementsLoaded = false;
    private FlowLayout playerContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    public InvitablePlayersScreen(List<String> players) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("invitable_players_ui")));
        this.players = players;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .sizing(Sizing.fill(100));

        flowLayout.child(Containers.verticalScroll(Sizing.fill(50), Sizing.fill(100), this.playerContainer)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .positioning(Positioning.relative(50, 0)));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            for (String username : this.players) {
                this.playerContainer.child(getInvitablePlayerElement(username));
            }

            this.elementsLoaded = true;
        }
    }

    public FlowLayout getInvitablePlayerElement(String username) {
        FlowLayout element = this.model.expandTemplate(FlowLayout.class, "player-element",
                Map.of("username", username));

        element.childById(ButtonComponent.class, "invite-button").onPress(button -> {
            GPNetworking.GP_CHANNEL.clientHandle().send(new InvitePlayerPacket(username));
            button.active(false);
        });

        return element;
    }
}
