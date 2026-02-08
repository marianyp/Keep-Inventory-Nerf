package dev.mariany.keepinventorynerf.packet;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.packet.clientbound.DeathLossesPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public final class KINPackets {
    private KINPackets() {
    }

    public static void bootstrap() {
        KeepInventoryNerf.bootstrapLog("Packets");

        clientBound(PayloadTypeRegistry.playS2C());
        serverBound(PayloadTypeRegistry.playC2S());
    }

    private static void clientBound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(DeathLossesPacket.ID, DeathLossesPacket.CODEC);
    }

    private static void serverBound(PayloadTypeRegistry<RegistryByteBuf> registry) {
    }
}
