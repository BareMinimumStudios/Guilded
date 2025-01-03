package keno.guildedparties.client.screens.own_guild.management;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.client.screens.ActionConfirmScreen;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.networking.packets.serverbound.ChangeGuildSettingsPacket;

public class GuildSettingsMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private boolean isGuildPrivate;
    private double managePlayerRankPriority;
    private double managePlayerPriority;
    private double manageGuildPriority;
    private double invitePlayersPriority;
    private final boolean hasCustomTextures;

    public GuildSettingsMenu(String guildName, GuildSettings currentSettings) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("settings_menu_ui")));
        this.guildName = guildName;

        this.isGuildPrivate = currentSettings.isPrivate();
        this.managePlayerRankPriority = currentSettings.managePlayerRankPriority();
        this.managePlayerPriority = currentSettings.managePlayerPriority();
        this.manageGuildPriority = currentSettings.manageGuildPriority();
        this.invitePlayersPriority = currentSettings.invitePlayersPriority();
        this.hasCustomTextures = currentSettings.hasCustomTextures();
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(SmallCheckboxComponent.class, "is-private-checkbox")
                .checked(this.isGuildPrivate).onChanged()
                .subscribe(changed -> this.isGuildPrivate = !this.isGuildPrivate);

        flowLayout.childById(DiscreteSliderComponent.class, "player-rank-priority-slider")
                .setFromDiscreteValue(this.managePlayerRankPriority).onChanged()
                .subscribe(value -> this.managePlayerRankPriority = value);

        flowLayout.childById(DiscreteSliderComponent.class, "manage-player-priority-slider")
                .setFromDiscreteValue(this.managePlayerPriority).onChanged()
                .subscribe(value -> this.managePlayerPriority = value);

        flowLayout.childById(DiscreteSliderComponent.class, "manage-guild-priority-slider")
                .setFromDiscreteValue(this.manageGuildPriority).onChanged()
                .subscribe(value -> this.manageGuildPriority = value);

        flowLayout.childById(DiscreteSliderComponent.class, "invite-players-priority-slider")
                .setFromDiscreteValue(this.invitePlayersPriority).onChanged()
                .subscribe(value -> this.invitePlayersPriority = value);

        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .onPress(button -> {
                    GuildSettings newSettings = new GuildSettings(this.isGuildPrivate,
                            (int) this.managePlayerRankPriority, (int) this.managePlayerPriority,
                            (int) this.manageGuildPriority, (int) this.invitePlayersPriority,
                            this.hasCustomTextures);

                    this.client.setScreen(new ActionConfirmScreen<>("change the guild's settings",
                            new ChangeGuildSettingsPacket(this.guildName, newSettings)));
                });
    }
}
