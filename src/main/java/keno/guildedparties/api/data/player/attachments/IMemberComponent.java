package keno.guildedparties.api.data.player.attachments;

import dev.onyxstudios.cca.api.v3.component.Component;
import keno.guildedparties.api.data.Rank;
import keno.guildedparties.api.data.player.Member;

public interface IMemberComponent extends Component {
    Member getMemberData();
    void changeMemberData(Member member);

    interface MemberDataUpdater {
        Member updateData(String guildKey, Rank rank);
    }
}
