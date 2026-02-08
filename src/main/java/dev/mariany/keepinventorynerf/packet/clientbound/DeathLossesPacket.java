package dev.mariany.keepinventorynerf.packet.clientbound;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record DeathLossesPacket(int levels, List<ItemStack> stacks) implements CustomPayload {
    public static final Id<DeathLossesPacket> ID = new Id<>(
            KeepInventoryNerf.id("death_losses")
    );

    public static final PacketCodec<RegistryByteBuf, DeathLossesPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, DeathLossesPacket::levels,
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), DeathLossesPacket::stacks,
            DeathLossesPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
