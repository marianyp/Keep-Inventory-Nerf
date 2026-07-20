package dev.mariany.keepinventorynerf.mixin;

import dev.mariany.keepinventorynerf.logic.DeathLossesHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin {
    @Inject(method = "dropEquipment", at = @At(value = "TAIL"))
    protected void injectDropEquipment(ServerLevel level, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (player instanceof ServerPlayer serverPlayer) {
            DeathLossesHandler.dropAndNotify(serverPlayer);
        }
    }
}
