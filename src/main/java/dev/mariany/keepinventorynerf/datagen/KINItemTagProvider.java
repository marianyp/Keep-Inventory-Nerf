package dev.mariany.keepinventorynerf.datagen;

import dev.mariany.keepinventorynerf.tag.KINTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class KINItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public KINItemTagProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> completableFuture
    ) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(KINTags.Items.IGNORE_DROP).add(Items.RECOVERY_COMPASS.builtInRegistryHolder().key());
    }
}
