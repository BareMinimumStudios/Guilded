package keno.guildedparties.impl.integration.crafttweaker.extend.guilds;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.GuildBanList;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;

//I'm not trusting scripts with banning and unbanning
@ZenRegister(loaders = "*")
@Document("guilded/guilds/GuildBanList")
@NativeTypeRegistration(value = GuildBanList.class, zenCodeName = "guilded.guilds.GuildBanList")
public class CTGuildBanList {
    @ZenCodeType.Method
    @ZenCodeType.Getter("bannedPlayers")
    public static List<String> getBannedPlayers(GuildBanList internal) {
        return internal.getBannedPlayers();
    }

    @ZenCodeType.Method
    public static boolean isPlayerBanned(GuildBanList internal, String username) {
        return internal.isPlayerBanned(username);
    }
}
*///?}

//? if >1.21.1
public class CTGuildBanList {}