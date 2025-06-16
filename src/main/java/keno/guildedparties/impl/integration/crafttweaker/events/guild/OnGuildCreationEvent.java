package keno.guildedparties.impl.integration.crafttweaker.events.guild;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.GuildEvents;
import keno.guildedparties.impl.data.guilds.Guild;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/guild/OnGuildCreationEvent")
@ZenCodeType.Name("guilded.api.events.guild.OnGuildCreationEvent")
public final class OnGuildCreationEvent {
    @ZenEvent.Bus
    public static final IEventBus<OnGuildCreationEvent> BUS = IEventBus.direct(OnGuildCreationEvent.class,
            FabricEventBusWire.of(GuildEvents.ON_GUILD_CREATION, GuildEvents.OnGuildCreationCallback.class, OnGuildCreationEvent.class));

    private final ServerPlayerEntity leader;
    private final Guild guild;

    private OnGuildCreationEvent(ServerPlayerEntity leader, Guild guild) {
        this.leader = leader;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static OnGuildCreationEvent of(final ServerPlayerEntity leader, final Guild guild) {
        return new OnGuildCreationEvent(leader, guild);
    }

    @ZenCodeType.Getter("guild")
    public Guild getGuild() {
        return this.guild;
    }

    @ZenCodeType.Getter("leader")
    public ServerPlayerEntity getPlayer() {
        return this.leader;
    }
}
*///?}
//? if >1.21.1
public final class OnGuildCreationEvent {}
