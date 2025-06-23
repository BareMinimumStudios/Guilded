package keno.guildedparties.impl.data.guilds.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.UUID;

public record GuildItemList(UUID uuid, List<Identifier> ids) {
    public GuildItemList(List<Identifier> ids) {
        this(UUID.randomUUID(), ids);
    }

    public static final Codec<GuildItemList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("listNumber").forGetter(GuildItemList::uuid),
            Identifier.CODEC.listOf().fieldOf("ids").forGetter(GuildItemList::ids)
    ).apply(instance, GuildItemList::new));

    public void addIds(List<Identifier> ids) {
        this.ids.addAll(ids);
    }

    public void removeIds(List<Identifier> ids) {
        this.ids.removeAll(ids);
    }

    public void removeIds(GuildItemList list) {
        removeIds(list.ids());
    }

    public void addIds(GuildItemList list) {
        addIds(list.ids());
    }

    public boolean isGuildItem(Item item) {
        ItemStack defaultStack = item.getDefaultStack();
        RegistryEntry<Item> entry = defaultStack.getRegistryEntry();
        for (Identifier id : ids) {
            if (entry.matchesId(id)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return ids.toString();
    }
}
