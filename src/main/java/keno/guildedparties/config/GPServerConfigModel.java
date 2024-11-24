package keno.guildedparties.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.RestartRequired;

@Config(name = "gp-server-config", wrapperName = "GPServerConfig")
public class GPServerConfigModel {
    @RestartRequired
    public boolean enableServerCommands = false;
}
