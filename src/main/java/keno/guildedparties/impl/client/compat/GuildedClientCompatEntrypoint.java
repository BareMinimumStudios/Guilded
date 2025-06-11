package keno.guildedparties.impl.client.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface GuildedClientCompatEntrypoint {
    void initClient();
}
