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
@Document("guilded/api/events/guild/OnGuildClosureEvent")
@ZenCodeType.Name("guilded.api.events.guild.OnGuildClosureEvent")
public final class OnGuildClosureEvent {
    @ZenEvent.Bus
    public static final IEventBus<OnGuildClosureEvent> BUS = IEventBus.direct(OnGuildClosureEvent.class,
            FabricEventBusWire.of(GuildEvents.ON_GUILD_CLOSURE, GuildEvents.OnGuildClosureCallback.class, OnGuildClosureEvent.class));

    private final ServerPlayerEntity leader;
    private final Guild guild;

    private OnGuildClosureEvent(ServerPlayerEntity leader, Guild guild) {
        this.leader = leader;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static OnGuildClosureEvent of(final ServerPlayerEntity leader, final Guild guild) {
        return new OnGuildClosureEvent(leader, guild);
    }

    @ZenCodeType.Getter("leader")
    public ServerPlayerEntity getLeader() {
        return leader;
    }

    @ZenCodeType.Getter("guild")
    public Guild getGuild() {
        return guild;
    }
}
*///?}
//? if >1.21.1
public final class OnGuildClosureEvent {}