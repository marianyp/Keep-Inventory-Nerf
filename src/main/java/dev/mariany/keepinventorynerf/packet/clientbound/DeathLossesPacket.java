package dev.mariany.keepinventorynerf.packet.clientbound;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record DeathLossesPacket(int levels, List<ItemStack> stacks) implements CustomPacketPayload {
    public static final Type<DeathLossesPacket> ID = new Type<>(
            KeepInventoryNerf.id("death_losses")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DeathLossesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DeathLossesPacket::levels,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), DeathLossesPacket::stacks,
            DeathLossesPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
