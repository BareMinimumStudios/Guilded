package keno.guildedparties.impl.integration.crafttweaker.events.guild.item;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredReveal;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.items.GuildItemEvents;
import keno.guildedparties.api.events.items.ItemListContainer;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/guild/item/AddGuildItemsEvent")
@ZenCodeType.Name("guilded.api.events.guild.item.AddGuildItemsEvent")
public final class AddGuildItemsEvent {
    @ZenEvent.Bus
    public static final IEventBus<AddGuildItemsEvent> BUS = IEventBus.direct(AddGuildItemsEvent.class,
            FabricEventBusWire.of(GuildItemEvents.ADD, GuildItemEvents.AddGuildItemsCallback.class, AddGuildItemsEvent.class));

    private ItemListContainer container;

    private AddGuildItemsEvent(ItemListContainer container) {
        this.container = container;
    }

    @FabricWiredWrap
    public static AddGuildItemsEvent of(final ItemListContainer container) {
        return new AddGuildItemsEvent(container);
    }

    @FabricWiredReveal
    public static ItemListContainer reveal(final AddGuildItemsEvent event) {
        return event.getContainer();
    }

    @ZenCodeType.Getter("container")
    public ItemListContainer getContainer() {
        return this.container;
    }

    @ZenCodeType.Setter("container")
    public void setContainer(ItemListContainer container) {
        this.container = container;
    }
}
*///?}
//? if >1.21.1
public final class AddGuildItemsEvent {}
