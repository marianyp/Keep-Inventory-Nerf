package dev.mariany.keepinventorynerf.client.packet;


import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import dev.mariany.keepinventorynerf.packet.clientbound.DeathLossesPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ClientBoundPackets {
    private ClientBoundPackets() {
    }

    public static void bootstrap() {
        KeepInventoryNerf.bootstrapLog("Client Bound Packets");

        ClientPlayNetworking.registerGlobalReceiver(DeathLossesPacket.ID, ClientBoundPackets::handleDroppedStacks);
    }

    private static void handleDroppedStacks(DeathLossesPacket payload, ClientPlayNetworking.Context context) {
        KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.updateLostLevels(payload.levels());
        KeepInventoryNerfClient.DROPPED_ITEMS_RENDERER.updateDroppedStacks(payload.stacks());
    }
}
