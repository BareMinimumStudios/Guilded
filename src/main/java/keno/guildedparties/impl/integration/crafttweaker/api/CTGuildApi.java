package keno.guildedparties.impl.integration.crafttweaker.api;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.GuildApi;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.GuildBanList;
import keno.guildedparties.impl.data.guilds.GuildSettings;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Optional;

@ZenRegister(loaders = "*")
@Document("guilded/api/GuildApi")
@ZenCodeType.Name("guilded.api.GuildApi")
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

    @ZenCodeType.Method
    public GuildSettings getSettings(MinecraftServer server, String guildName) {
        return GuildApi.getSettings(server, guildName);
    }

    @ZenCodeType.Method
    public GuildSettings getSettings(MinecraftServer server, Guild guild) {
        return GuildApi.getSettings(server, guild);
    }

    @ZenCodeType.Method
    public GuildBanList getBanList(MinecraftServer server, String guildName) {
        return GuildApi.getBanList(server, guildName);
    }

    @ZenCodeType.Method
    public GuildBanList getBanList(MinecraftServer server, Guild guild) {
        return GuildApi.getBanList(server, guild);
    }

    @ZenCodeType.Method
    public Optional<GuildItemList> getItemList(MinecraftServer server, String guildName) {
        return GuildApi.getGuildItems(server, guildName);
    }
}
*///?}

//? if >1.21.1
public class CTGuildApi {}
