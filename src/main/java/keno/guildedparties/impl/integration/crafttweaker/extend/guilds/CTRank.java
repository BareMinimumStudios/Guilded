package keno.guildedparties.impl.integration.crafttweaker.extend.guilds;
//? if =1.21.1 {

/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.Rank;
import org.openzen.zencode.java.ZenCodeType;
import static com.blamejared.crafttweaker_annotations.annotations.NativeConstructor.ConstructorParameter;

@ZenRegister
@Document("guilded/data/guilds/Rank")
@NativeTypeRegistration(value = Rank.class, zenCodeName = "guilded.data.guilds.Rank",
        constructors = @NativeConstructor(
                value = {@ConstructorParameter(type = String.class, name = "name"),
                        @ConstructorParameter(type = Integer.class, name = "priority")}))
public class CTRank {
    @ZenCodeType.Getter("priority")
    public static int getPriority(Rank internal) {
        return internal.priority();
    }

    @ZenCodeType.Getter("name")
    public static String getName(Rank internal) {
        return internal.name();
    }

    @ZenCodeType.Method
    public static boolean isCoLeader(Rank internal) {
        return internal.isCoLeader();
    }
}
*///?}

//? if > 1.21.1
public class CTRank {}
