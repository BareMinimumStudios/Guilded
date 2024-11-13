package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;

public class GuildedMenuScreen extends BaseUIModelScreen<FlowLayout> {
    public GuildedMenuScreen() {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guilded_menu_ui")));
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "view-guilds-button").onPress(button -> {
            GuildedParties.LOGGER.info("View guilds button has been pressed");
        });
        flowLayout.childById(ButtonComponent.class, "create-guild-button").onPress(button -> {
            GuildedParties.LOGGER.info("Create guild button has been pressed");
        });
    }
}
