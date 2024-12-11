package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextAreaComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.packets.serverbound.CreateGuildPacket;
import net.minecraft.client.resource.language.I18n;

public class CreateGuildMenu extends BaseUIModelScreen<FlowLayout> {
    private String guildName = "";
    private String leaderRankName = "";
    private String description = "";

    private boolean guildNameUpdated = false;
    private boolean leaderNameUpdated = false;
    private boolean descriptionUpdated = false;


    public CreateGuildMenu() {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("create_guild_ui")));
    }

    @Override
    protected void build(FlowLayout layout) {
        layout.childById(ButtonComponent.class, "back")
                .onPress(button -> this.client.setScreen(new GuildedMenuScreen(false)));

        layout.childById(TextBoxComponent.class, "guild-name")
                .text(I18n.hasTranslation("gui.guildedparties.guild_name") ? I18n.translate("gui.guildedparties.guild_name") : "Guild name")
                .onChanged().subscribe(text -> {
                    this.guildName = text;
                    this.guildNameUpdated = true;
                });

        layout.childById(TextBoxComponent.class, "leader-rank-name")
                .text(I18n.hasTranslation("gui.guildedparties.leader_role_name") ? I18n.translate("gui.guildedparties.leader_role_name") : "Leader role name")
                .onChanged().subscribe(text -> {
                    this.leaderRankName = text;
                    this.leaderNameUpdated = true;
                });

        layout.childById(TextAreaComponent.class, "description")
                .text(I18n.hasTranslation("gui.guildedparties.description") ? I18n.translate("gui.guildedparties.description") : "")
                .onChanged().subscribe(text -> {
                    this.description = text;
                    this.descriptionUpdated = true;
                });

        layout.childById(ButtonComponent.class, "confirm-button")
                .active(false)
                .onPress(button -> this.client.setScreen(new ActionConfirmScreen<>("create a guild",
                        new CreateGuildPacket(this.guildName, this.leaderRankName, this.description))));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.uiAdapter == null) return;

        if (canPressConfirm()) {
            this.uiAdapter.rootComponent.childById(ButtonComponent.class, "confirm-button")
                    .active(true);
        }
    }

    private boolean canPressConfirm() {
        return this.descriptionUpdated && this.leaderNameUpdated && this.guildNameUpdated;
    }
}
