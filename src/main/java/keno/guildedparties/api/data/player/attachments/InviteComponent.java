package keno.guildedparties.api.data.player.attachments;

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer;
import com.bibireden.data_attributes.endec.nbt.NbtSerializer;
import keno.guildedparties.api.data.player.Invite;
import net.minecraft.nbt.NbtCompound;

public class InviteComponent implements IInviteComponent {
    private Invite invite = null;

    public InviteComponent(Invite invite) {
        this.invite = invite;
    }

    public InviteComponent() {

    }

    @Override
    public Invite getInvite() {
        return this.invite;
    }

    @Override
    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if (nbtCompound.contains("invite"))
            this.invite = Invite.ENDEC.decodeFully(NbtDeserializer::of, nbtCompound.get("invite"));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        if (this.invite != null)
            nbtCompound.put("invite", Invite.ENDEC.encodeFully(NbtSerializer::of, this.invite));
    }
}
