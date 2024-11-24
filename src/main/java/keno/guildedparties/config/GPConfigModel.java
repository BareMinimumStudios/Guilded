package keno.guildedparties.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = "guildedparties")
@Config(name = "gp-config", wrapperName = "GPConfig")
public class GPConfigModel {
    @RestartRequired
    public boolean enableServerCommands = false;

    @Sync(Option.SyncMode.INFORM_SERVER)
    @PredicateConstraint("lacksIllegalCharacters")
    public String guildToAutoJoin = "Insert Guild Name Here";

    @Sync(Option.SyncMode.INFORM_SERVER)
    public boolean wantsToAutoJoinGuild = false;

    public static boolean lacksIllegalCharacters(String guildName) {
        return !guildName.contains(Character.toString(','));
    }
}
