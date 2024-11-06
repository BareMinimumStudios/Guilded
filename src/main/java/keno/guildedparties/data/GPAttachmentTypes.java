package keno.guildedparties.data;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.player.Member;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public class GPAttachmentTypes {
    public static final AttachmentType<Member> MEMBER_ATTACHMENT = AttachmentRegistry.create(GuildedParties.GPLoc("member_attachment"),
            memberBuilder -> {
        memberBuilder.copyOnDeath();
        memberBuilder.persistent(Member.codec);
    });
}
