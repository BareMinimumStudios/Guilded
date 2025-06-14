package keno.guildedparties.impl.data.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class GuildBanList {
    public static Codec<GuildBanList> codec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("bannedPlayers").forGetter(GuildBanList::getBannedPlayers)
    ).apply(instance, GuildBanList::new));

    private final List<String> bannedPlayers = new ArrayList<>();

    public GuildBanList(List<String> list) {
        this.bannedPlayers.addAll(list);
    }

    public boolean isPlayerBanned(String username) {
        return bannedPlayers.contains(username);
    }

    public void banPlayer(String username) {
        if (!this.bannedPlayers.contains(username)) {
             this.bannedPlayers.add(username);
        }
    }

    public void unbanPlayer(String string) {
        this.bannedPlayers.remove(string);
    }

    public List<String> getBannedPlayers() {
        return bannedPlayers;
    }
}
