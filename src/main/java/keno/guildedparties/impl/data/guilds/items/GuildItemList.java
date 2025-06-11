package keno.guildedparties.impl.data.guilds.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;

public record GuildItemList(List<Identifier> ids) {
    public static final Codec<GuildItemList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.listOf().fieldOf("ids").forGetter(GuildItemList::ids)
    ).apply(instance, GuildItemList::new));

    public void addIds(Identifier... ids) {
        this.ids.addAll(List.of(ids));
    }

    public void removeIds(Identifier... ids) {
        this.ids.removeAll(List.of(ids));
    }

    public void subtractIds(GuildItemList list) {
        removeIds(list.ids().toArray(new Identifier[0]));
    }

    public void addIds(GuildItemList list) {
        addIds(list.ids().toArray(new Identifier[0]));
    }

    public boolean isGuildItem(Item item) {
        ItemStack defaultStack = item.getDefaultStack();
        RegistryEntry<Item> entry = defaultStack.getRegistryEntry();
        for (Identifier id : ids) {
            if (entry.matchesId(id)) return true;
        }
        return false;
    }
}
