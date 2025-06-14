package keno.guildedparties.impl.integration.crafttweaker.extend.guilds;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.GuildSettings;
import org.openzen.zencode.java.ZenCodeGlobals;
import org.openzen.zencode.java.ZenCodeType;

import static com.blamejared.crafttweaker_annotations.annotations.NativeConstructor.ConstructorParameter;

@ZenRegister(loaders = "*")
@Document("guilded/guilds/GuildSettings")
@NativeTypeRegistration(value = GuildSettings.class, zenCodeName = "guilded.guilds.GuildSettings",
    constructors = @NativeConstructor(value = {
            @ConstructorParameter(type = Boolean.class, name = "isPrivate"),
            @ConstructorParameter(type = Integer.class, name = "managePlayerRankPriority"),
            @ConstructorParameter(type = Integer.class, name = "managePlayerPriority"),
            @ConstructorParameter(type = Integer.class, name = "manageGuildPriority"),
            @ConstructorParameter(type = Integer.class, name = "invitePlayersPriority"),
            @ConstructorParameter(type = Boolean.class, name = "hasCustomTextures")
    }))
public class CTGuildSettings {
    @ZenCodeType.Method
    @ZenCodeGlobals.Global("defaultGuildSettings")
    public static GuildSettings getDefaultSettings() {
        return GuildSettings.getDefaultSettings();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static boolean isPrivate(GuildSettings internal) {
        return internal.isPrivate();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static int managePlayerRankPriority(GuildSettings internal) {
        return internal.managePlayerRankPriority();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static int managePlayerPriority(GuildSettings internal) {
        return internal.managePlayerPriority();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static int manageGuildPriority(GuildSettings internal) {
        return internal.manageGuildPriority();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static int invitePlayersPriority(GuildSettings internal) {
        return internal.invitePlayersPriority();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter
    public static boolean hasCustomTextures(GuildSettings internal) {
        return internal.hasCustomTextures();
    }
}
*///?}

//? if >1.21.1
public class CTGuildSettings {}