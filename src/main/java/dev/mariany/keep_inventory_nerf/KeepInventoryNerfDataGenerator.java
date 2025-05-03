package dev.mariany.keep_inventory_nerf;

import dev.mariany.keep_inventory_nerf.datagen.KeepInventoryNerfItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class KeepInventoryNerfDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(KeepInventoryNerfItemTagProvider::new);
    }
}
