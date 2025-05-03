package dev.mariany.keep_inventory_nerf.datagen;

import dev.mariany.keep_inventory_nerf.tag.KeepInventoryNerfTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class KeepInventoryNerfItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public KeepInventoryNerfItemTagProvider(FabricDataOutput output,
                                            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(KeepInventoryNerfTags.Items.IGNORE_DROP).add(Items.RECOVERY_COMPASS);
    }
}
