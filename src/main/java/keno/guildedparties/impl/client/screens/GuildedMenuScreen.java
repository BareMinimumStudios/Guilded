package keno.guildedparties.impl.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.client.custom.abstract_screen.DefaultSurfaceRetriever;
import keno.guildedparties.impl.networking.GPNetworking;
import keno.guildedparties.impl.networking.packets.serverbound.GetGuildInfosPacket;
import keno.guildedparties.impl.networking.packets.serverbound.GetOwnGuildPacket;
import keno.guildedparties.impl.networking.packets.serverbound.QuickJoinPacket;

public class GuildedMenuScreen extends BaseUIModelScreen<FlowLayout> implements DefaultSurfaceRetriever {
    public boolean isInGuild;

    public GuildedMenuScreen(boolean isInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guilded_menu_ui")));
        this.isInGuild = isInGuild;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(FlowLayout.class, "main").surface(getDefaultSurface());

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
