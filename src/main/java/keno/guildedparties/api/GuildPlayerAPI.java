package keno.guildedparties.api;

import keno.guildedparties.impl.data.GPAttachmentTypes;
import keno.guildedparties.impl.data.player.Member;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

/** Use this for player-related actions on common-side
 * @see GuildApi guild api
 * @see GuildServerPlayerApi server-sided player api**/
public class GuildPlayerAPI {

    /** Checks if player is in a guild via attachment data existence.
     See {@link #getPlayerData(PlayerEntity)} for retrieving attachment data
     * @param player the player checked
     * @return true if attachment data is present, otherwise it returns false **/
    public static boolean isPlayerInGuild(PlayerEntity player) {
        return player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
    }

    /** Retrieves an optional that will contain a player's member data is it exists
     * @see Member
     * @param player the player data is retrieved from
     * @return an optional containing the member data if it exists, empty otherwise **/
    public static Optional<Member> getPlayerData(PlayerEntity player) {
        if (isPlayerInGuild(player)) return Optional.ofNullable(player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT));

        return Optional.empty();
    }
}
