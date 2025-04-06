package keno.guildedparties.api.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.data.player.attachments.GCToggleComponent;
import keno.guildedparties.api.data.player.attachments.InviteComponent;
import keno.guildedparties.api.data.player.attachments.MemberComponent;

public final class GPComponents implements EntityComponentInitializer {
    public static final ComponentKey<MemberComponent> MEMBER_KEY
            = ComponentRegistry.getOrCreate(GuildedParties.modLoc("member"), MemberComponent.class);
    public static final ComponentKey<InviteComponent> INVITE_KEY
            = ComponentRegistry.getOrCreate(GuildedParties.modLoc("invite"), InviteComponent.class);
    public static final ComponentKey<GCToggleComponent> GC_KEY
            = ComponentRegistry.getOrCreate(GuildedParties.modLoc("gc_toggle"), GCToggleComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MEMBER_KEY, player -> new MemberComponent(), RespawnCopyStrategy.ALWAYS_COPY);

        registry.registerForPlayers(INVITE_KEY, player -> new InviteComponent(), RespawnCopyStrategy.ALWAYS_COPY);

        registry.registerForPlayers(GC_KEY, player -> new GCToggleComponent(false), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
