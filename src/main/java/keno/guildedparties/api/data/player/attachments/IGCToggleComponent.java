package keno.guildedparties.api.data.player.attachments;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IGCToggleComponent extends Component {
    void setToggle(boolean toggled);
    boolean isToggled();
}
