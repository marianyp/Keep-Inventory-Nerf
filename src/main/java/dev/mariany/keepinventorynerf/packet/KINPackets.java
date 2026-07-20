package dev.mariany.keepinventorynerf.packet;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.packet.clientbound.DeathLossesPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class KINPackets {
    private KINPackets() {
    }

    public static void bootstrap() {
        KeepInventoryNerf.bootstrapLog("Packets");
        clientBound(PayloadTypeRegistry.clientboundPlay());
        serverBound(PayloadTypeRegistry.serverboundPlay());
    }

    private static void clientBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(DeathLossesPacket.ID, DeathLossesPacket.CODEC);
    }

    private static void serverBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
    }
}
