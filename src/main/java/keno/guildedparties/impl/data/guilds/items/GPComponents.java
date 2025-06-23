package keno.guildedparties.impl.data.guilds.items;

import com.mojang.serialization.Codec;
import keno.guildedparties.GuildedParties;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class GPComponents {
    public static final ComponentType<List<String>> GUILD_COMPONENT =
            register("guild_component", ComponentType.<List<String>>builder().codec(Codec.STRING.listOf()).build());

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
