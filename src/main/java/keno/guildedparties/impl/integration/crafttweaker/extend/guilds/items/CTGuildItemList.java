package keno.guildedparties.impl.integration.crafttweaker.extend.guilds.items;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor.ConstructorParameter;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.minecraft.util.Identifier;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.UUID;

@ZenRegister(loaders = "*")
@Document("guilded/guilds/items/GuildItemList")
@NativeTypeRegistration(value = GuildItemList.class, zenCodeName = "guilded.guilds.items.GuildItemList",
    constructors = @NativeConstructor(value = {
            @ConstructorParameter(type = List.class, name = "itemIds")
    }))
public class CTGuildItemList {
    @ZenCodeType.Method
    public static void addIds(GuildItemList internal, List<Identifier> ids) {
        internal.addIds(ids);
    }

    @ZenCodeType.Method
    public static void addIds(GuildItemList internal, GuildItemList list) {
        internal.addIds(list);
    }

    @ZenCodeType.Method
    public static void removeIds(GuildItemList internal, List<Identifier> ids) {
        internal.removeIds(ids);
    }

    @ZenCodeType.Method
    public static void removeIds(GuildItemList internal, GuildItemList list) {
        internal.removeIds(list);
    }
}
*///?}

//? if >1.21.1
public class CTGuildItemList {}