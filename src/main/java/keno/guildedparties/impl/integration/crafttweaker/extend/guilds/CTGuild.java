package keno.guildedparties.impl.integration.crafttweaker.extend.guilds;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.Rank;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.Map;

@ZenRegister(loaders = "*")
@Document("guilded/guilds/Guild")
@NativeTypeRegistration(value = Guild.class, zenCodeName = "guilded.guilds.Guild")
public class CTGuild {
    @ZenCodeType.Method
    public static Rank getPlayerRank(Guild internal, String username) {
        return internal.getPlayerRank(username);
    }

    @ZenCodeType.Method
    public static Rank getPlayerRank(Guild internal, ServerPlayerEntity player) {
        return internal.getPlayerRank(player);
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("ranks")
    public static List<Rank> getRanks(Guild internal) {
        return internal.getRanks();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("getName")
    public static String getName(Guild internal) {
        return internal.getName();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("players")
    public static Map<String, Rank> getPlayers(Guild internal) {
        return internal.getPlayers();
    }

    @ZenCodeType.Getter("description")
    public static String getDescription(Guild internal) {
        return internal.getDescription();
    }

    @ZenCodeType.Setter("description")
    public static void setDescription(Guild internal, String description) {
        internal.setDescription(description);
    }

    @ZenCodeType.Method
    public static int demotePlayer(Guild internal, MinecraftServer server, String username) {
        return internal.demoteMember(server, username);
    }

    @ZenCodeType.Method
    public static int demotePlayer(Guild internal, ServerPlayerEntity player) {
        return internal.demoteMember(player);
    }

    @ZenCodeType.Method
    public static int promotePlayer(Guild internal, ServerPlayerEntity player) {
        return internal.promoteMember(player);
    }

    @ZenCodeType.Method
    public static int promotePlayer(Guild internal, MinecraftServer server, String username) {
        return internal.promotePlayer(server, username);
    }

    @ZenCodeType.Method
    public static int changeMemberRank(Guild internal, MinecraftServer server, String username, String rankName) {
        return internal.changeMemberRank(server, username, rankName);
    }

    @ZenCodeType.Method
    public static int changeMemberRank(Guild internal, ServerPlayerEntity player, String rankName) {
        return internal.changeMemberRank(player, rankName);
    }

    @ZenCodeType.Method
    public static int changeMemberRank(Guild internal, ServerPlayerEntity player, Rank rank) {
        return internal.changeMemberRank(player, rank);
    }

    @ZenCodeType.Method
    public static int changeMemberRank(Guild internal, MinecraftServer server, String username, Rank rank) {
        return internal.changeMemberRank(server, username, rank);
    }

    @ZenCodeType.Method
    public static void addPlayerToGuild(Guild internal, ServerPlayerEntity player, String rankName) {
        internal.addPlayerToGuild(player, rankName);
    }

    @ZenCodeType.Method
    public static void removePlayerFromGuild(Guild internal, ServerPlayerEntity player) {
        internal.removePlayerFromGuild(player);
    }

    @ZenCodeType.Method
    public static void removePlayerFromGuild(Guild internal, MinecraftServer server, String username) {
        internal.removePlayerFromGuild(server, username);
    }

    @ZenCodeType.Method
    public static boolean isPlayerInGuild(Guild internal, String username) {
        return internal.isPlayerInGuild(username);
    }

    @ZenCodeType.Method
    public static boolean isPlayerInGuild(Guild internal, ServerPlayerEntity player) {
        return internal.isPlayerInGuild(player);
    }

    @ZenCodeType.Method
    public static int addRank(Guild internal, Rank rank) {
        return internal.addRank(rank);
    }

    @ZenCodeType.Method
    public static int removeRank(Guild internal, Rank rank) {
        return internal.removeRank(rank);
    }

    @ZenCodeType.Method
    public static int removeRank(Guild internal, String rankName) {
        return internal.removeRank(rankName);
    }
}
*///?}

//? if > 1.21.1
public class CTGuild {}