package keno.guildedparties.impl.integration.crafttweaker.api;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.GuildApi;
import keno.guildedparties.impl.data.guilds.Guild;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Optional;

@ZenRegister(loaders = "*")
@ZenCodeType.Name("guilded.api.GuildApi")
@Document("guilded/api/GuildApi")
public class CTGuildApi {
    @ZenCodeGlobals.Global("guildApi")
    public static final CTGuildApi API = new CTGuildApi();

    private CTGuildApi() {}

    @ZenCodeType.Method
    public void broadcastToGuildmates(MinecraftServer server, Guild guild, Text text) {
        GuildApi.broadcastToGuildmates(server, guild, text);
    }

    @ZenCodeType.Method
    public void broadcastToGuildmates(MinecraftServer server, Guild guild, String message) {
        GuildApi.broadcastToGuildmates(server, guild, message);
    }

    @ZenCodeType.Method
    public void broadcastToGuildmates(ServerPlayerEntity player, String message) {
        GuildApi.broadcastToGuildmates(player, message);
    }

    @ZenCodeType.Method
    public Optional<Guild> getGuild(MinecraftServer server, String guildName) {
        return GuildApi.getGuild(server, guildName);
    }

    @ZenCodeType.Method
    public Optional<Guild> getGuild(ServerPlayerEntity player) {
        return GuildApi.getGuild(player);
    }


}
*///?}

//? if >1.21.1
public class CTGuildApi {}
