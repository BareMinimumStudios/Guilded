package keno.guildedparties.api.data.player.attachments;

import dev.onyxstudios.cca.api.v3.component.Component;
import keno.guildedparties.api.data.player.Invite;

public interface IInviteComponent extends Component {
    Invite getInvite();
    void setInvite(Invite invite);
}
