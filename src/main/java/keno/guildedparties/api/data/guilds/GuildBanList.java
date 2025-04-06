package keno.guildedparties.api.data.guilds;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.ArrayList;
import java.util.List;

public record GuildBanList(List<String> players) {
    public GuildBanList() {
        this(new ArrayList<>());
    }

    public static StructEndec<GuildBanList> ENDEC = StructEndecBuilder.of(
        StructEndec.STRING.listOf().optionalFieldOf("players", GuildBanList::players, List.of()),
    GuildBanList::new);

    public boolean isPlayerBanned(String userName) {
        return players.contains(userName);
    }

    public void banPlayer(String username) {
        if (!this.players.contains(username)) {
            this.players.add(username);
        }
    }

    public void unbanPlayer(String string) {
        this.players.remove(string);
    }
}
