package keno.guildedparties.api.data.player.attachments;

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer;
import com.bibireden.data_attributes.endec.nbt.NbtSerializer;
import io.wispforest.endec.StructEndec;
import net.minecraft.nbt.NbtCompound;

public class GCToggleComponent implements IGCToggleComponent {
    private boolean isToggled;

    public GCToggleComponent(boolean toggled) {
        this.isToggled = toggled;
    }

    @Override
    public void setToggle(boolean toggled) {
        this.isToggled = toggled;
    }

    @Override
    public boolean isToggled() {
        return isToggled;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        this.isToggled = StructEndec.BOOLEAN.decodeFully(NbtDeserializer::of, nbtCompound.get("toggled"));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.put("toggled", StructEndec.BOOLEAN.encodeFully(NbtSerializer::of, this.isToggled));
    }
}
