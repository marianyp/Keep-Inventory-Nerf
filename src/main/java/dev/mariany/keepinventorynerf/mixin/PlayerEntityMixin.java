package dev.mariany.keepinventorynerf.mixin;

import dev.mariany.keepinventorynerf.logic.DeathLossesHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropInventory", at = @At(value = "TAIL"))
    protected void dropInventory(ServerWorld world, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            DeathLossesHandler.tryDrop(serverPlayer);
        }
    }
}
