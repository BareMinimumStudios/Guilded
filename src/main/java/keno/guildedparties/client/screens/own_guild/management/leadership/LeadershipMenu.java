package keno.guildedparties.client.screens.own_guild.management.leadership;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.packets.serverbound.DisbandGuildPacket;

import java.util.List;
import java.util.Map;

public class LeadershipMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final Map<String, Rank> players;

    public LeadershipMenu(String guildName, Map<String, Rank> players) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("leadership_menu_ui")));
        this.guildName = guildName;
        this.players = players;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "disband-button")
                .onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("disband the guild",
                        new DisbandGuildPacket(this.guildName))));

        flowLayout.childById(ButtonComponent.class, "step-down-button")
                .onPress(button -> this.client.setScreen(new StepDownMenu(this.guildName, this.players)));
    }
}
