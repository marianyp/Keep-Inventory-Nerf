package dev.mariany.keepinventorynerf.tag;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class KINTags {
    private KINTags() {
    }

    public static final class Items {
        private Items() {
        }

        public static TagKey<Item> IGNORE_DROP = createTag("ignore_drop");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, KeepInventoryNerf.id(name));
        }
    }
}
