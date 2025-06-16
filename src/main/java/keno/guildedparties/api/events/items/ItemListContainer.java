package keno.guildedparties.api.events.items;

import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.minecraft.util.Identifier;

// To simplify adding guild item lists
public record ItemListContainer(Identifier guildId, GuildItemList list) {

}
