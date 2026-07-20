package dev.mariany.keepinventorynerf.mixin;

import dev.mariany.keepinventorynerf.logic.ExperienceHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "restoreFrom", at = @At(value = "TAIL"))
    public void injectRestoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayer newPlayer = (ServerPlayer) (Object) this;
        ExperienceHandler.updatePlayerExperience(oldPlayer, newPlayer, alive);
    }
}
