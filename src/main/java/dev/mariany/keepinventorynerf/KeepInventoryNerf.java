package dev.mariany.keepinventorynerf;

import dev.mariany.keepinventorynerf.gamerule.KINGamerules;
import dev.mariany.keepinventorynerf.packet.KINPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepInventoryNerf implements ModInitializer {
    public static final String MOD_ID = "keepinventorynerf";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    public static void bootstrapLog(String type) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            LOGGER.info("Registering {}", type);
        }
    }

    @Override
    public void onInitialize() {
        KINPackets.bootstrap();
        KINGamerules.bootstrap();
    }
}