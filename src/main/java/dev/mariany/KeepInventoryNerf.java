package dev.mariany;

import dev.mariany.gamerule.KeepInventoryNerfGamerules;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepInventoryNerf implements ModInitializer {
    public static final String MOD_ID = "keep_inventory_nerf";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        KeepInventoryNerfGamerules.bootstrap();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}