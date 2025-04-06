package keno.guildedparties.api.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Sync;
import keno.guildedparties.GuildedParties;

@Modmenu(modId = GuildedParties.MOD_ID)
@Config(name = "gp-config", wrapperName = "GPConfig")
public class GPConfigModel {
    @RestartRequired
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableServerCommands = false;

    @Sync(Option.SyncMode.INFORM_SERVER)
    public String guildToQuickJoin = "";
}
