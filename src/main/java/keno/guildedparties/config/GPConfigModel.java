package keno.guildedparties.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = "guildedparties")
@Config(name = "gp-config", wrapperName = "GPConfig")
public class GPConfigModel {
    @RestartRequired
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableServerCommands = false;
}
