package dev.mariany.keepinventorynerf.logic;

import dev.mariany.keepinventorynerf.gamerule.KINGamerules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;

public final class ExperienceHandler {
    private ExperienceHandler() {
    }

    public static void updatePlayerExperience(
            ServerPlayerEntity oldPlayer,
            ServerPlayerEntity newPlayer,
            boolean alive
    ) {
        if (alive) {
            return;
        }

        ServerWorld world = newPlayer.getEntityWorld();
        GameRules gameRules = world.getGameRules();

        if (gameRules.getBoolean(GameRules.KEEP_INVENTORY)) {
            resetExperience(newPlayer);

            newPlayer.addExperience(getKeptXp(oldPlayer));
        }
    }

    public static int getKeptXp(ServerPlayerEntity player) {
        double percentage = (double) getKeptXpPercent(player) / 100;
        return MathHelper.floor(player.totalExperience * percentage);
    }

    private static int getKeptXpPercent(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        GameRules gameRules = world.getGameRules();
        return 100 - gameRules.getInt(KINGamerules.EXPERIENCE_LOSS_PERCENTAGE);
    }

    private static void resetExperience(PlayerEntity player) {
        player.experienceLevel = 0;
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.setScore(0);
    }
}
