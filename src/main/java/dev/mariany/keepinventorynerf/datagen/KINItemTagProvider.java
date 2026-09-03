package dev.mariany.keepinventorynerf.datagen;

import dev.mariany.keepinventorynerf.tag.KINTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class KINItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public KINItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture
    ) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(KINTags.Items.IGNORE_DROP).add(Items.RECOVERY_COMPASS);
    }
}
