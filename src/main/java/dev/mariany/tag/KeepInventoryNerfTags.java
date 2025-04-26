package dev.mariany.tag;

import dev.mariany.KeepInventoryNerf;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class KeepInventoryNerfTags {
    public static class Items {
        public static TagKey<Item> IGNORE_DROP = createTag("ignore_drop");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, KeepInventoryNerf.id(name));
        }
    }
}
