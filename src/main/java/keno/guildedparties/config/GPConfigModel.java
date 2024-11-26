package keno.guildedparties.config;

import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = "guildedparties")
@Config(name = "gp-config", wrapperName = "GPConfig")
public class GPConfigModel {
    @RestartRequired
    public boolean enableServerCommands = false;
}
