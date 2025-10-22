package keno.guildedparties.api.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.Option.SyncMode;
import io.wispforest.owo.config.annotation.*;
import keno.guildedparties.impl.client.custom.surface.DefaultSurface;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "guildedparties")
@Config(name = "gp-config", wrapperName = "GPConfig")
public class GPConfigModel {
    @SectionHeader("server")
    @RestartRequired
    @ExcludeFromScreen
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableServerCommands = false;

    @SectionHeader("client")
    @Sync(Option.SyncMode.INFORM_SERVER)
    public String guildToQuickJoin = "";

    @Sync(SyncMode.NONE)
    public DefaultSurface defaultUIStyle = DefaultSurface.CLASSIC;

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
