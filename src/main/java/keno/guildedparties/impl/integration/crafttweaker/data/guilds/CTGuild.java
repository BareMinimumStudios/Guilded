package keno.guildedparties.impl.integration.crafttweaker.data.guilds;

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

@ZenRegister
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
    @ZenCodeType.Setter("setName")
    public static void setName(Guild internal, String name) {
        internal.setName(name);
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("players")
    public static Map<String, Rank> getPlayers(Guild internal) {
        return internal.getPlayers();
    }

    @ZenCodeType.Method
    public static int demotePlayer(Guild internal, MinecraftServer server, String username) {
        return internal.demoteMember(server, username);
    }

    @ZenCodeType.Method
    public static int demotePlayer(Guild internal, ServerPlayerEntity player) {
        return internal.demoteMember(player);
    }

}
*///?}

//? if > 1.21.1
public class CTGuild {}