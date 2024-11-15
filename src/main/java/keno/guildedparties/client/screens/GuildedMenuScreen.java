package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.GetOwnGuildPacket;

public class GuildedMenuScreen extends BaseUIModelScreen<FlowLayout> {
    public boolean isInGuild;

    public GuildedMenuScreen(boolean isInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guilded_menu_ui")));
        this.isInGuild = isInGuild;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "view-guilds-button").onPress(button -> {
            GuildedParties.LOGGER.info("View guilds button has been pressed");
        });
        flowLayout.childById(ButtonComponent.class, "create-guild-button").onPress(button -> {
            if (isInGuild) {
                GPNetworking.GP_CHANNEL.clientHandle().send(new GetOwnGuildPacket());
            } else {
                GuildedParties.LOGGER.info("Player is not in a guild");
            }
        });
    }
}
