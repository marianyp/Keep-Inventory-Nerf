package dev.mariany.mixin;

import dev.mariany.KeepInventoryNerfHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom", at = @At(value = "TAIL"))
    public void injectCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;

        if (!alive && newPlayer.getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            newPlayer.experienceLevel = 0;
            newPlayer.totalExperience = 0;
            newPlayer.experienceProgress = 0;
            newPlayer.setScore(0);

            int experienceToGive = KeepInventoryNerfHelper.getPercentageOfExperience(
                    KeepInventoryNerfHelper.getTotalExperienceForLevel(oldPlayer.experienceLevel), 66.666D);
            newPlayer.addExperience(experienceToGive);
        }
    }
}
