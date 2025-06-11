package keno.guildedparties.impl.data.guilds.items;

import keno.guildedparties.GuildedParties;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class GPComponents {
    public static final ComponentType<List<Identifier>> GUILD_COMPONENT =
            register("guild_component", ComponentType.<List<Identifier>>builder().codec(Identifier.CODEC.listOf()).build());

    public static void init() {

    }

    public static <E> ComponentType<E> register(String id, ComponentType<E> type) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                GuildedParties.GPLoc(id),
                type
        );
    }
}
