package dev.mariany.keepinventorynerf.tag;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class KINTags {
    private KINTags() {
    }

    public static final class Items {
        private Items() {
        }

        public static TagKey<Item> IGNORE_DROP = createTag("ignore_drop");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, KeepInventoryNerf.id(name));
        }
    }
}
