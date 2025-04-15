package keno.guildedparties.api.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.networking.GPNetworking;
import keno.guildedparties.api.networking.packets.serverbound.NoParamServerPacket;

public class GuildedMenuScreen extends BaseUIModelScreen<FlowLayout> {
    public boolean isInGuild;

    public GuildedMenuScreen(boolean isInGuild) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.modLoc("guilded_menu_ui")));
        this.isInGuild = isInGuild;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "quick-join").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new NoParamServerPacket("guild",
                NoParamServerPacket.QUICK_JOIN)));

        flowLayout.childById(ButtonComponent.class, "view-guilds-button").onPress(button
                -> GPNetworking.GP_CHANNEL.clientHandle().send(new NoParamServerPacket("guild",
                NoParamServerPacket.GET_GUILD_INFO)));

        flowLayout.childById(ButtonComponent.class, "create-guild-button").onPress(button -> {
            if (isInGuild) {
                GPNetworking.GP_CHANNEL.clientHandle().send(new NoParamServerPacket("guild",
                        NoParamServerPacket.GET_OWN_GUILD));
            } else {
                this.client.setScreen(new CreateGuildMenu());
            }
        });
    }
}
