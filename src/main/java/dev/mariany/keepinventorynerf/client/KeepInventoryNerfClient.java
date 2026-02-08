package dev.mariany.keepinventorynerf.client;

import dev.mariany.keepinventorynerf.client.packet.ClientBoundPackets;
import dev.mariany.keepinventorynerf.client.gui.screen.DeathLossesRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeepInventoryNerfClient implements ClientModInitializer {
    public static final DeathLossesRenderer DROPPED_ITEMS_RENDERER = new DeathLossesRenderer();

    @Override
    public void onInitializeClient() {
        ClientBoundPackets.bootstrap();
    }
}
