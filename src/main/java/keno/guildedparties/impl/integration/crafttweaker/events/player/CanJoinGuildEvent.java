package keno.guildedparties.impl.integration.crafttweaker.events.player;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.event.ZenEvent;
import com.blamejared.crafttweaker.api.event.bus.FabricEventBusWire;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredReveal;
import com.blamejared.crafttweaker.api.event.bus.FabricWiredWrap;
import com.blamejared.crafttweaker.api.event.bus.IEventBus;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import keno.guildedparties.api.events.GuildPlayerEvents;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.integration.crafttweaker.events.guild.CanCreateGuildEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenEvent
@Document("guilded/api/event/player/CanJoinGuildEvent")
@ZenCodeType.Name("guilded.api.event.player.CanJoinGuildEvent")
public final class CanJoinGuildEvent {
    @ZenEvent.Bus
    public static final IEventBus<CanJoinGuildEvent> BUS = IEventBus.direct(CanJoinGuildEvent.class,
            FabricEventBusWire.of(GuildPlayerEvents.CAN_JOIN_GUILD, GuildPlayerEvents.CanJoinGuildCallback.class, CanJoinGuildEvent.class));

    private final ServerPlayerEntity player;
    private final Guild guild;
    private boolean canJoinGuild = true;

    private CanJoinGuildEvent(ServerPlayerEntity player, Guild guild) {
        this.player = player;
        this.guild = guild;
    }

    @FabricWiredWrap
    public static CanJoinGuildEvent of(final ServerPlayerEntity player, final Guild guild) {
        return new CanJoinGuildEvent(player, guild);
    }

    @FabricWiredReveal
    public static boolean reveal(final CanCreateGuildEvent event) {
        return event.canCreate();
    }

    @ZenCodeType.Getter("player")
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @ZenCodeType.Getter("guild")
    public Guild getGuild() {
        return guild;
    }

    @ZenCodeType.Setter("canJoinGuild")
    public void canJoinGuild(boolean canJoin) {
        this.canJoinGuild = canJoin;
    }

    @ZenCodeType.Getter
    public boolean canJoinGuild() {
        return this.canJoinGuild;
    }
}
*///?}
//? if >1.21.1
public final class CanJoinGuildEvent {}