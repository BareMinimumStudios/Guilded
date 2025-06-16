package keno.guildedparties.impl.integration.crafttweaker.events.player;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.GuildPlayerEvents;
import keno.guildedparties.impl.data.guilds.Guild;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/player/OnGuildJoinEvent")
@ZenCodeType.Name("guilded.api.events.player.OnGuildJoinEvent")
public final class OnGuildJoinEvent {
    @ZenEvent.Bus
    public static final IEventBus<OnGuildJoinEvent> BUS = IEventBus.direct(OnGuildJoinEvent.class,
            FabricEventBusWire.of(GuildPlayerEvents.ON_GUILD_JOIN, GuildPlayerEvents.OnGuildJoinCallback.class, OnGuildJoinEvent.class));

    private final ServerPlayerEntity player;
    private final Guild guild;

    private OnGuildJoinEvent(ServerPlayerEntity player, Guild guild) {
        this.player = player;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static OnGuildJoinEvent of(final ServerPlayerEntity player, final Guild guild) {
        return new OnGuildJoinEvent(player, guild);
    }

    @ZenCodeType.Getter("player")
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @ZenCodeType.Getter("guild")
    public Guild getGuild() {
        return guild;
    }
}
*///?}
//? if >1.21.1
public final class OnGuildJoinEvent {}