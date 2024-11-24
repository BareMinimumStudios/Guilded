package keno.guildedparties.client.screens.own_guild.management.leadership;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.packets.serverbound.StepDownPacket;

import java.util.Map;

public class StepDownMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final Map<String, Rank> players;

    private String selectedPlayer = "";
    private String clientUsername = "";

    private boolean elementsLoaded = false;

    public StepDownMenu(String guildName, Map<String, Rank> players) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("step_down_ui")));
        this.guildName = guildName;
        this.players = players;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .active(false).onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("step down",
                        new StepDownPacket(this.guildName, this.selectedPlayer))));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!elementsLoaded) {
            this.clientUsername = this.client.player.getGameProfile().getName();

            for (String username : players.keySet()) {
                if (!this.clientUsername.equals(username)) {
                    this.uiAdapter.rootComponent.childById(FlowLayout.class, "player-container")
                            .child(getPlayerElement(username, players.get(username)));
                }
            }

            elementsLoaded = true;
        }
    }

    private FlowLayout getPlayerElement(String username, Rank rank) {
        FlowLayout element = this.model.expandTemplate(FlowLayout.class, "player-element",
                Map.of("username", username, "rank", rank.name()));

        element.childById(ButtonComponent.class, "select-button")
                .onPress(button -> {
                    this.selectedPlayer = username;
                    this.uiAdapter.rootComponent.forEachDescendant(component -> {
                        if (component instanceof ButtonComponent buttonComponent) {
                            buttonComponent.active(true);
                        }
                    });
                    button.active(false);
                });

        return element;
    }
}
