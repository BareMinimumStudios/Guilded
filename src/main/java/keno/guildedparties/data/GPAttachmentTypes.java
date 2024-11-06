package keno.guildedparties.data;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.player.Member;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class GPAttachmentTypes {
    public static final AttachmentType<Member> MEMBER_ATTACHMENT = AttachmentRegistry.<Member>builder()
            .copyOnDeath().persistent(Member.codec).buildAndRegister(GuildedParties.GPLoc("member_attachment"));

    public static void init() {}
}
