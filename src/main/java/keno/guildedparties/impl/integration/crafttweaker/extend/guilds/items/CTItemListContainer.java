package keno.guildedparties.impl.integration.crafttweaker.extend.guilds.items;

//? if = 1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeConstructor;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import keno.guildedparties.api.events.items.ItemListContainer;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;

@ZenRegister
@Document("guilded/guilds/items/ItemListContainer")
@NativeTypeRegistration(value = ItemListContainer.class, zenCodeName = "guilded.guilds.items.ItemListContainer",
constructors = @NativeConstructor(value = {
        @NativeConstructor.ConstructorParameter(type = String.class, name = "guildName"),
        @NativeConstructor.ConstructorParameter(type = GuildItemList.class, name = "guildItemList")
}))
*///?}
public class CTItemListContainer {

}