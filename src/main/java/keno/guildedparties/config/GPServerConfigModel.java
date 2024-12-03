package keno.guildedparties.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.RestartRequired;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Config(name = "gp-server-config", wrapperName = "GPServerConfig")
public class GPServerConfigModel {
    @RestartRequired
    public boolean enableServerCommands = false;
}
