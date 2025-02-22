package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.serverbound.GetGuildInfosPacket;
import keno.guildedparties.networking.packets.serverbound.GetOwnGuildPacket;
import keno.guildedparties.networking.packets.serverbound.QuickJoinPacket;

public class GuildedMenuScreen extends BaseUIModelScreen<FlowLayout> {
    public boolean isInGuild;

    public GuildedMenuScreen(boolean isInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guilded_menu_ui")));
        this.isInGuild = isInGuild;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "quick-join").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new QuickJoinPacket()));

        flowLayout.childById(ButtonComponent.class, "view-guilds-button").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetGuildInfosPacket()));

        flowLayout.childById(ButtonComponent.class, "create-guild-button").onPress(button -> {
            if (isInGuild) {
                GPNetworking.GP_CHANNEL.clientHandle().send(new GetOwnGuildPacket());
            } else {
                this.client.setScreen(new CreateGuildMenu());
            }
        });
    }
}
