package dev.mariany.keepinventorynerf.client;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.client.gui.screen.DeathLossesRenderer;
import dev.mariany.keepinventorynerf.client.packet.ClientBoundPackets;
import dev.mariany.keepinventorynerf.config.ConfigHandler;
import dev.mariany.keepinventorynerf.config.KINClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeepInventoryNerfClient implements ClientModInitializer {
    public static final DeathLossesRenderer DROPPED_ITEMS_RENDERER = new DeathLossesRenderer();

    private static final ConfigHandler<KINClientConfig> CONFIG_HANDLER = new ConfigHandler<>(
            KeepInventoryNerf.MOD_ID + "-client",
            new KINClientConfig()
    );

    public static KINClientConfig getConfig() {
        return CONFIG_HANDLER.getConfig();
    }

    @Override
    public void onInitializeClient() {
        CONFIG_HANDLER.loadConfig();
        ClientBoundPackets.bootstrap();
    }
}
