package keno.guildedparties.client.screens.view_guilds;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.clientbound.ViewGuildsPacket;
import keno.guildedparties.networking.packets.serverbound.GetGuildInfosPacket;
import keno.guildedparties.networking.packets.serverbound.JoinGuildPacket;

import java.util.Map;

public class ViewGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final String leaderName;
    private final int members;
    private final String description;
    private final boolean isInGuild;
    private final boolean isPrivate;

    private boolean elementsLoaded = false;

    protected ViewGuildMenu(String guildName, String leaderName, int members, String description,
                            boolean isInGuild, boolean isPrivate) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("view_guild_ui")));
        this.guildName = guildName;
        this.leaderName = leaderName;
        this.members = members;
        this.description = description;
        this.isInGuild = isInGuild;
        this.isPrivate = isPrivate;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(ButtonComponent.class, "back")
                .onPress(button -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetGuildInfosPacket()));

        flowLayout.childById(ButtonComponent.class, "join-button")
                .active(!isInGuild && !isPrivate).onPress(button
                        -> this.client.setScreen(new ActionConfirmScreen<>("join this guild",
                        new JoinGuildPacket(this.guildName))));

        flowLayout.childById(ButtonComponent.class, "relations-button")
                .active(false);
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            this.uiAdapter.rootComponent.childById(FlowLayout.class, "container")
                    .child(getElement());

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getElement() {
        return this.model.expandTemplate(FlowLayout.class, "guild-description",
                Map.of("guild-name", this.guildName,
                        "leader-name", this.leaderName,
                        "members", String.valueOf(this.members),
                        "description", this.description));
    }
}
