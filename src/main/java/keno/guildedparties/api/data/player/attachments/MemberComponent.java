package keno.guildedparties.api.data.player.attachments;

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer;
import com.bibireden.data_attributes.endec.nbt.NbtSerializer;
import keno.guildedparties.api.data.player.Member;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class MemberComponent implements IMemberComponent {
    private Member member = null;

    public MemberComponent(Member member) {
        this.member = member;
    }

    public MemberComponent() {

    }

    public boolean hasMemberData() {
        return member != null;
    }

    @Override
    public Member getMemberData() {
        return member;
    }

    @Override
    public void changeMemberData(@Nullable Member member) {
        this.member = member;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if (nbtCompound.contains("member_data"))
            this.member = Member.ENDEC.decodeFully(NbtDeserializer::of, nbtCompound.get("member_data"));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        if (this.member != null)
            nbtCompound.put("member_data", Member.ENDEC.encodeFully(NbtSerializer::of, this.member));
    }

    public boolean isCoLeader() {
        return this.member.getRank().isCoLeader();
    }
}
