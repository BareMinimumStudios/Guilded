package keno.guildedparties.impl.integration.crafttweaker.extend.player;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.Rank;
import keno.guildedparties.impl.data.player.Member;
import org.openzen.zencode.java.ZenCodeType;

import static com.blamejared.crafttweaker_annotations.annotations.NativeConstructor.ConstructorParameter;

@ZenRegister
@Document("guilded/data/player/Member")
@NativeTypeRegistration(value = Member.class, zenCodeName = "guilded.data.player.Member",
    constructors = @NativeConstructor(value = {
            @ConstructorParameter(type = String.class, name = "guildName"),
            @ConstructorParameter(type = Rank.class, name = "rank")
    }))
public class CTMember {
    @ZenCodeType.Getter("guildName")
    public static String getGuildName(Member internal) {
        return internal.getGuildKey();
    }

    @ZenCodeType.Getter("rank")
    public static Rank getRank(Member internal) {
        return internal.getRank();
    }
}
*///?}

//? if > 1.21.1
public class CTMember {}