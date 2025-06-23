package keno.guildedparties.impl.integration.crafttweaker.events.guild.item;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.items.GuildItemEvents;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/guild/item/ModifyGuildItemsEvent")
@ZenCodeType.Name("guilded.api.events.guild.item.ModifyGuildItemsEvent")
public final class ModifyGuildItemsEvent {
    @ZenEvent.Bus
    public static final IEventBus<ModifyGuildItemsEvent> BUS = IEventBus.direct(ModifyGuildItemsEvent.class,
            FabricEventBusWire.of(GuildItemEvents.MODIFY, GuildItemEvents.ModifyGuildItemsCallback.class, ModifyGuildItemsEvent.class));

    private final String guildName;
    private final GuildItemList list;

    private ModifyGuildItemsEvent(String guildName, GuildItemList list) {
        this.guildName = guildName;
        this.list = list;
    }

    @FabricWiredWrap
    public static ModifyGuildItemsEvent of(final String guildName, final GuildItemList list) {
        return new ModifyGuildItemsEvent(guildName, list);
    }

    @ZenCodeType.Getter("guildName")
    public String getGuildId() {
        return guildName;
    }

    @ZenCodeType.Getter("itemList")
    public GuildItemList getList() {
        return list;
    }
}
*///?}
//? if >1.21.1
public final class ModifyGuildItemsEvent {}