package keno.guildedparties.data.guilds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildBanList {
    public static Codec<GuildBanList> codec = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.listOf().fieldOf("bannedPlayers").forGetter(GuildBanList::getBannedPlayers)
    ).apply(instance, GuildBanList::new));

    private final List<UUID> bannedPlayers = new ArrayList<>();

    public GuildBanList(List<UUID> list) {
        this.bannedPlayers.addAll(list);
    }

    public boolean isPlayerBanned(UUID playerId) {
        return bannedPlayers.contains(playerId);
    }

    public void banPlayer(UUID playerId) {
        if (!this.bannedPlayers.contains(playerId)) {
             this.bannedPlayers.add(playerId);
        }
    }

    public void unbanPlayer(UUID playerId) {
        this.bannedPlayers.remove(playerId);
    }

    public List<UUID> getBannedPlayers() {
        return bannedPlayers;
    }
}
