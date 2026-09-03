package dev.mariany.keepinventorynerf.logic;

import com.google.common.math.IntMath;
import dev.mariany.keepinventorynerf.gamerule.KINGamerules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.rule.GameRules;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

        if (!gameRules.getValue(GameRules.KEEP_INVENTORY)) {
            return;
        }

        resetExperience(newPlayer);
        newPlayer.addExperience(getKeptXp(oldPlayer));
    }

    private static void resetExperience(PlayerEntity player) {
        player.experienceLevel = 0;
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.setScore(0);
    }

    private static int getKeptXp(ServerPlayerEntity player) {
        double percentage = (double) getKeptXpPercent(player) / 100;
        return MathHelper.floor(getCurrentXp(player) * percentage);
    }

    private static int getKeptXpPercent(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        GameRules gameRules = world.getGameRules();
        return 100 - gameRules.getValue(KINGamerules.EXPERIENCE_LOSS_PERCENTAGE);
    }

    private static int getCurrentXp(PlayerEntity player) {
        int xpAtCurrentLevel = getXpForLevel(player.experienceLevel);
        int xpRequiredForNextLevel = getNextLevelExperience(player.experienceLevel);
        return xpAtCurrentLevel + MathHelper.floor(player.experienceProgress * xpRequiredForNextLevel);
    }

    private static int getXpForLevel(int level) {
        if (level >= 32) {
            return MathHelper.floor(4.5 * level * level - 162.5 * level + 2220);
        }

        if (level >= 17) {
            return MathHelper.floor(2.5 * level * level - 40.5 * level + 360);
        }

        return level * level + 6 * level;
    }

    public static int getKeptLevels(ServerPlayerEntity player) {
        return calculateLevel(getKeptXp(player));
    }

    private static int calculateLevel(int experience) {
        float initialExperienceProgress = (float) experience / getNextLevelExperience(0);

        AtomicReference<Float> experienceProgress = new AtomicReference<>(initialExperienceProgress);
        AtomicInteger experienceLevel = new AtomicInteger();

        while (experienceProgress.get() < 0) {
            float xpRemainder = experienceProgress.get() * getNextLevelExperience(experienceLevel);

            if (experienceLevel.get() > 0) {
                addExperienceLevels(experienceLevel, experienceProgress, -1);
                experienceProgress.set(1 + xpRemainder / getNextLevelExperience(experienceLevel));
            } else {
                addExperienceLevels(experienceLevel, experienceProgress, -1);
                experienceProgress.set(0F);
            }
        }

        while (experienceProgress.get() >= 1) {
            experienceProgress.getAndUpdate(progress -> (progress - 1) * getNextLevelExperience(experienceLevel));
            addExperienceLevels(experienceLevel, experienceProgress, 1);
            experienceProgress.getAndUpdate(progress -> progress / getNextLevelExperience(experienceLevel));
        }

        return experienceLevel.get();
    }

    private static int getNextLevelExperience(AtomicInteger level) {
        return getNextLevelExperience(level.get());
    }

    private static int getNextLevelExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }

        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    private static void addExperienceLevels(
            AtomicInteger experienceLevel,
            AtomicReference<Float> experienceProgress,
            int levels
    ) {
        experienceLevel.getAndUpdate(level -> IntMath.saturatedAdd(level, levels));

        if (experienceLevel.get() >= 0) {
            return;
        }

        experienceLevel.set(0);
        experienceProgress.set(0F);
    }
}
