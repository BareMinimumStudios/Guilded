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
import keno.guildedparties.impl.data.guilds.Rank;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/events/player/OnLeavingGuildEvent")
@ZenCodeType.Name("guilded.api.events.player.OnLeavingGuildEvent")
public final class OnLeavingGuildEvent {
    @ZenEvent.Bus
    public static final IEventBus<OnLeavingGuildEvent> BUS = IEventBus.direct(OnLeavingGuildEvent.class,
            FabricEventBusWire.of(GuildPlayerEvents.ON_LEAVING_GUILD, GuildPlayerEvents.OnLeavingGuildCallback.class, OnLeavingGuildEvent.class));

    private final ServerPlayerEntity player;
    private final Rank rank;
    private final Guild guild;

    private OnLeavingGuildEvent(ServerPlayerEntity player, Rank rank, Guild guild) {
        this.player = player;
        this.rank = rank;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static OnLeavingGuildEvent of(final ServerPlayerEntity player, final Rank rank, final Guild guild) {
        return new OnLeavingGuildEvent(player, rank, guild);
    }

    @ZenCodeType.Getter("player")
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @ZenCodeType.Getter("guild")
    public Guild getGuild() {
        return guild;
    }

    @ZenCodeType.Getter("rank")
    public Rank getRank() {
        return rank;
    }
}
*///?}
//? if >1.21.1
public final class OnLeavingGuildEvent {}