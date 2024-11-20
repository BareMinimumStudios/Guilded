package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.networking.packets.serverbound.ChangeGuildSettingsPacket;
import keno.guildedparties.utils.MathUtil;

public class GuildSettingsMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private boolean isGuildPrivate;
    private double managePlayerRankPriority;
    private double managePlayerPriority;
    private double manageGuildPriority;
    private double invitePlayersPriority;

    public GuildSettingsMenu(String guildName, GuildSettings currentSettings) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("settings_menu_ui")));
        this.guildName = guildName;

        this.isGuildPrivate = currentSettings.isPrivate();
        // Because of how values are handled in discrete sliders, we need to normalize our setting values before setting them as defaults
        this.managePlayerRankPriority = normalizeValues(currentSettings.managePlayerRankPriority());
        this.managePlayerPriority = normalizeValues(currentSettings.managePlayerPriority());
        this.manageGuildPriority = normalizeValues(currentSettings.manageGuildPriority());
        this.invitePlayersPriority = normalizeValues(currentSettings.invitePlayersPriority());
    }

    private static double normalizeValues(int value) {
        return MathUtil.normalizeValues(value, 1, 50);
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(SmallCheckboxComponent.class, "is-private-checkbox")
                .checked(this.isGuildPrivate).onChanged()
                .subscribe(changed -> this.isGuildPrivate = !this.isGuildPrivate);

        flowLayout.childById(DiscreteSliderComponent.class, "player-rank-priority-slider")
                .value(this.managePlayerRankPriority).onChanged()
                .subscribe(value -> this.managePlayerRankPriority = (int) value);

        flowLayout.childById(DiscreteSliderComponent.class, "manage-player-priority-slider")
                .value(this.managePlayerPriority).onChanged()
                .subscribe(value -> this.managePlayerPriority = (int) value);

        flowLayout.childById(DiscreteSliderComponent.class, "manage-guild-priority-slider")
                .value(this.manageGuildPriority).onChanged()
                .subscribe(value -> this.manageGuildPriority = (int) value);

        flowLayout.childById(DiscreteSliderComponent.class, "invite-players-priority-slider")
                .value(this.invitePlayersPriority).onChanged()
                .subscribe(value -> this.invitePlayersPriority = (int) value);

        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .onPress(button -> {
                    GuildSettings newSettings = new GuildSettings(this.isGuildPrivate,
                            (int) this.managePlayerRankPriority, (int) this.managePlayerPriority,
                            (int) this.manageGuildPriority, (int) this.invitePlayersPriority);

                    this.client.setScreen(new ActionConfirmScreen<>("change the guild's settings",
                            new ChangeGuildSettingsPacket(this.guildName, newSettings)));
                });
    }
}
