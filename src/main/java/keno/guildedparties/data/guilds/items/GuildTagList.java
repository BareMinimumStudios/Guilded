package keno.guildedparties.data.guilds.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public record GuildTagList(List<TagKey<Item>> tags) {
    public static final Codec<GuildTagList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TagKey.codec(RegistryKeys.ITEM).listOf().fieldOf("tags").forGetter(GuildTagList::tags)
    ).apply(instance, GuildTagList::new));

    public void addTag(TagKey<Item> tag) {
        tags.add(tag);
    }

    public void removeTag(TagKey<Item> tagKey) {
        tags.remove(tagKey);
    }

    public void subtractTags(GuildTagList list) {
        for (TagKey<Item> key : list.tags()) {
            if (this.tags.contains(key)) tags.remove(key);
        }
    }

    public void addTags(GuildTagList list) {
        for (TagKey<Item> key : list.tags()) {
            if (!this.tags.contains(key)) tags.add(key);
        }
    }

    public boolean isGuildItem(Item item) {
        ItemStack defaultStack = item.getDefaultStack();
        for (TagKey<Item> tag : tags) {
            if (defaultStack.isIn(tag)) return true;
        }
        return false;
    }
}
