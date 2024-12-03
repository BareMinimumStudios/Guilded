package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.packets.serverbound.CreateGuildPacket;

public class CreateGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private String guildName = "";
    private String leaderRankName = "";

    public CreateGuildMenu() {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("create_guild_ui")));
    }

    @Override
    protected void build(FlowLayout layout) {
        layout.childById(TextBoxComponent.class, "guild-name")
                .text("Guild name here")
                .onChanged().subscribe(value -> {
                    this.guildName = value;
                    setConfirmToActive(layout);
                });

        layout.childById(TextBoxComponent.class, "leader-rank-name")
                .text("Leader rank name here")
                .onChanged().subscribe(value -> {
                    this.leaderRankName = value;
                    setConfirmToActive(layout);
                });

        layout.childById(ButtonComponent.class, "confirm-button")
                .active(false)
                .onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("create a guild",
                        new CreateGuildPacket(this.guildName, this.leaderRankName))));
    }

    private void setConfirmToActive(FlowLayout layout) {
        layout.childById(ButtonComponent.class, "confirm-button")
                .active(true);
    }
}
