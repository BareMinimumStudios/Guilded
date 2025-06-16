package keno.guildedparties.impl.integration.crafttweaker.api;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.GuildPlayerAPI;
import keno.guildedparties.api.GuildServerPlayerApi;
import keno.guildedparties.impl.data.guilds.Rank;
import keno.guildedparties.impl.data.player.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Optional;

/^*
 * This combines {@link keno.guildedparties.api.GuildPlayerAPI GuildPlayerApi} and
 * {@link keno.guildedparties.api.GuildServerPlayerApi GuildServerPlayerApi} into a singular
 * ZenCode object, as CraftTweaker is server-side only
 ^/
@ZenRegister
@Document("guilded/api/PlayerApi")
@ZenCodeType.Name("guilded.api.PlayerApi")
public class CTGuildPlayerApi {
    @ZenCodeGlobals.Global("guildPlayerApi")
    public static final CTGuildPlayerApi API = new CTGuildPlayerApi();

    private CTGuildPlayerApi() {

    }

    @ZenCodeType.Method
    public boolean isPlayerInGuild(ServerPlayerEntity player) {
        return GuildPlayerAPI.isPlayerInGuild(player);
    }

    @ZenCodeType.Method
    public Optional<Member> getPlayerData(ServerPlayerEntity player) {
        return GuildPlayerAPI.getPlayerData(player);
    }

    @ZenCodeType.Method
    public boolean addPlayerToGuild(ServerPlayerEntity player, String guildName) {
        return GuildServerPlayerApi.addPlayerToGuild(player, guildName);
    }

    @ZenCodeType.Method
    public boolean addPlayerToGuild(MinecraftServer server, String username, String guildName) {
        return GuildServerPlayerApi.addPlayerToGuild(server, username, guildName);
    }

    @ZenCodeType.Method
    public boolean removePlayerFromGuild(ServerPlayerEntity player) {
        return GuildServerPlayerApi.removePlayerFromGuild(player);
    }

    @ZenCodeType.Method
    public boolean removePlayerFromGuild(MinecraftServer server, String username) {
        return GuildServerPlayerApi.removePlayerFromGuild(server, username);
    }

    @ZenCodeType.Method
    public boolean promotePlayer(ServerPlayerEntity player) {
        return GuildServerPlayerApi.promotePlayer(player);
    }

    @ZenCodeType.Method
    public boolean promotePlayer(MinecraftServer server, String username) {
        return GuildServerPlayerApi.promotePlayer(server, username);
    }

    @ZenCodeType.Method
    public boolean demotePlayer(ServerPlayerEntity player) {
        return GuildServerPlayerApi.demotePlayer(player);
    }

    @ZenCodeType.Method
    public boolean demotePlayer(MinecraftServer server, String username) {
        return GuildServerPlayerApi.demotePlayer(server, username);
    }

    @ZenCodeType.Method
    public boolean changeRank(ServerPlayerEntity player, String rankName) {
        return GuildServerPlayerApi.changePlayerRank(player, rankName);
    }
    
    @ZenCodeType.Method
    public boolean changeRank(MinecraftServer server, String username, String rankName) {
        return GuildServerPlayerApi.changePlayerRank(server, username, rankName);
    }

    @ZenCodeType.Method
    public boolean changeRank(ServerPlayerEntity player, Rank rank) {
        return GuildServerPlayerApi.changePlayerRank(player, rank);
    }

    @ZenCodeType.Method
    public boolean changeRank(MinecraftServer server, String username, Rank rank) {
        return GuildServerPlayerApi.changePlayerRank(server, username, rank);
    }
}
*///?}
//? if >1.21.1
public class CTGuildPlayerApi {}