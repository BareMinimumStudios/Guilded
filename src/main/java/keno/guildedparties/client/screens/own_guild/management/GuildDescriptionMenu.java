package keno.guildedparties.client.screens.own_guild.management;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextAreaComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.networking.packets.serverbound.ChangeDescriptionPacket;

public class GuildDescriptionMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private String description;

    protected GuildDescriptionMenu(String guildName, String description) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("guild_description_ui")));
        this.guildName = guildName;
        this.description = description;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(TextAreaComponent.class, "description")
                .text(description).onChanged().subscribe(value -> this.description = value);

        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("change description",
                        new ChangeDescriptionPacket(this.guildName, this.description))));
    }
}
