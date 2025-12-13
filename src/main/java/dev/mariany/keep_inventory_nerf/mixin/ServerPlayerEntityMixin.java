package dev.mariany.keep_inventory_nerf.mixin;

import dev.mariany.keep_inventory_nerf.KeepInventoryNerfHelper;
import dev.mariany.keep_inventory_nerf.gamerule.KeepInventoryNerfGamerules;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
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

        ServerWorld world = newPlayer.getEntityWorld();
        GameRules gameRules = world.getGameRules();

        boolean keepInventory = gameRules.getBoolean(GameRules.KEEP_INVENTORY);
        int experiencePercentageLoss = gameRules.getInt(KeepInventoryNerfGamerules.EXPERIENCE_PERCENTAGE_LOSS);

        if (!alive && keepInventory) {
            newPlayer.experienceLevel = 0;
            newPlayer.totalExperience = 0;
            newPlayer.experienceProgress = 0;
            newPlayer.setScore(0);

            int percentage = 100 - experiencePercentageLoss;
            int totalXP = KeepInventoryNerfHelper.convertLevelsToExperience(oldPlayer.experienceLevel);
            int experienceToGive = MathHelper.floor((double) totalXP * percentage / 100);

            newPlayer.addExperience(experienceToGive);
        }
    }
}
