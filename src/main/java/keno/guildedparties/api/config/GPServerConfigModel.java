package keno.guildedparties.api.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.Option.SyncMode;
import io.wispforest.owo.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Config(name = "gp-server-config", wrapperName = "GPServerConfig")
public class GPServerConfigModel {
    @SectionHeader("server")
    @RestartRequired
    @ExcludeFromScreen
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableServerCommands = false;

    @SectionHeader("guildItems")
    @RestartRequired
    @Sync(SyncMode.OVERRIDE_CLIENT)
    public boolean enableGuildItems = true;

    @RestartRequired
    @Sync(SyncMode.NONE)
    public List<String> guildsWithItems = new ArrayList<>();

    @RestartRequired
    @Sync(SyncMode.NONE)
    public List<String> guildItems = new ArrayList<>();
}
