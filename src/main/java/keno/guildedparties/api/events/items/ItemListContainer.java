package keno.guildedparties.api.events.items;

import keno.guildedparties.impl.data.guilds.items.GuildItemList;

// To simplify adding guild item lists
public record ItemListContainer(String guildName, GuildItemList list) {

}
