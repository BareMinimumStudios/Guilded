package keno.guildedparties.impl.integration.crafttweaker.events.guild;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredReveal;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.GuildEvents;
import keno.guildedparties.impl.data.guilds.Guild;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/guild/CanCreateGuildEvent")
@ZenCodeType.Name("guilded.api.events.guild.CanCreateGuildEvent")
public final class CanCreateGuildEvent {
    @ZenEvent.Bus
    public static final IEventBus<CanCreateGuildEvent> BUS = IEventBus.direct(CanCreateGuildEvent.class,
            FabricEventBusWire.of(GuildEvents.CAN_CREATE_GUILD, GuildEvents.CanCreateGuildCallback.class, CanCreateGuildEvent.class));

    private final ServerPlayerEntity leader;
    private final Guild guild;
    private boolean canCreate = true;

    private CanCreateGuildEvent(ServerPlayerEntity leader, Guild guild) {
        this.leader = leader;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static CanCreateGuildEvent of(final ServerPlayerEntity leader, final Guild guild) {
        return new CanCreateGuildEvent(leader, guild);
    }

    @FabricWiredReveal
    public static boolean reveal(final CanCreateGuildEvent event) {
        return event.canCreate();
    }

    @ZenCodeType.Getter
    public boolean canCreate() {
        return this.canCreate;
    }

    @ZenCodeType.Setter("canCreate")
    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
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
public final class CanCreateGuildEvent {}