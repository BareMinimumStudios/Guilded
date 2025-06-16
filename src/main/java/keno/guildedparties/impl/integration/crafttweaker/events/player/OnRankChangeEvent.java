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
@Document("guilded/api/events/player/OnRankChangeEvent")
@ZenCodeType.Name("guilded.api.events.player.OnRankChangeEvent")
public final class OnRankChangeEvent {
    @ZenEvent.Bus
    public static final IEventBus<OnRankChangeEvent> BUS = IEventBus.direct(OnRankChangeEvent.class,
            FabricEventBusWire.of(GuildPlayerEvents.ON_RANK_CHANGE, GuildPlayerEvents.OnRankChangeCallback.class, OnRankChangeEvent.class));

    private final ServerPlayerEntity player;
    private final Rank rank;
    private final Guild guild;

    private OnRankChangeEvent(ServerPlayerEntity player, Rank rank, Guild guild) {
        this.player = player;
        this.rank = rank;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static OnRankChangeEvent of(final ServerPlayerEntity player, final Rank rank, final Guild guild) {
        return new OnRankChangeEvent(player, rank, guild);
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
public final class OnRankChangeEvent {}