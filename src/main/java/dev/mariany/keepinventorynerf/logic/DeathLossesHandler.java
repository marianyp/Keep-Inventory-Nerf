package dev.mariany.keepinventorynerf.logic;

import com.google.common.math.IntMath;
import dev.mariany.keepinventorynerf.gamerule.KINGamerules;
import dev.mariany.keepinventorynerf.mixin.accessor.PlayerEntityAccessor;
import dev.mariany.keepinventorynerf.packet.clientbound.DeathLossesPacket;
import dev.mariany.keepinventorynerf.tag.KINTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class DeathLossesHandler {
    private DeathLossesHandler() {
    }

    public static void dropAndNotify(ServerPlayerEntity player) {
        notifyClient(player, getLostXp(player), dropRandomItems(player));
    }

    private static void notifyClient(ServerPlayerEntity player, int lostXp, List<ItemStack> droppedStacks) {
        ServerPlayNetworking.send(player, new DeathLossesPacket(lostXp, droppedStacks));
    }

    private static int getLostXp(ServerPlayerEntity player) {
        if (!hasKeepInventory(player)) {
            return 0;
        }

        int keptXp = ExperienceHandler.getKeptXp(player);
        int newLevel = calculateLevel(keptXp);
        return player.experienceLevel - newLevel;
    }

    private static int calculateLevel(int experience) {
        AtomicInteger experienceLevel = new AtomicInteger();
        AtomicReference<Float> experienceProgress = new AtomicReference<>(
                (float) experience / getNextLevelExperience(experienceLevel)
        );

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

    private static int getNextLevelExperience(AtomicInteger experienceLevel) {
        int level = experienceLevel.get();

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

        if (experienceLevel.get() < 0) {
            experienceLevel.set(0);
            experienceProgress.set(0F);
        }
    }

    private static List<ItemStack> dropRandomItems(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();

        if (!hasKeepInventory(world)) {
            return List.of();
        }

        return dropRandomItems(player, getRandomDropCount(world));
    }

    private static int getRandomDropCount(ServerWorld world) {
        GameRules gameRules = world.getGameRules();

        int minItemsToDrop = gameRules.getInt(KINGamerules.KEEP_INVENTORY_MIN_ITEMS_TO_DROP);
        int maxItemsToDrop = gameRules.getInt(KINGamerules.KEEP_INVENTORY_MAX_ITEMS_TO_DROP);

        return world.getRandom().nextBetween(minItemsToDrop, maxItemsToDrop);
    }

    private static List<ItemStack> dropRandomItems(ServerPlayerEntity player, int dropAmount) {
        Random random = player.getRandom();
        PlayerInventory inventory = player.getInventory();

        List<Integer> slotIndexes = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();

        ((PlayerEntityAccessor) player).keepinventorynerf$vanishCursedItems();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);

            if (!itemStack.isEmpty() && !itemStack.isIn(KINTags.Items.IGNORE_DROP)) {
                slotIndexes.add(i);
            }
        }

        for (int i = 0; i < dropAmount; i++) {
            if (slotIndexes.isEmpty() || inventory.isEmpty()) {
                break;
            }

            int slotToRemove = slotIndexes.remove(random.nextInt(slotIndexes.size()));

            ItemStack stack = inventory.getStack(slotToRemove);

            stacks.add(stack.copy());

            dropItem(player, stack.copy());

            inventory.removeStack(slotToRemove);
        }

        return stacks;
    }

    private static void dropItem(ServerPlayerEntity player, ItemStack stack) {
        ServerWorld world = player.getEntityWorld();
        GameRules gameRules = world.getGameRules();
        Random random = world.getRandom();

        ItemEntity itemEntity = new ItemEntity(
                world,
                player.getX(),
                player.getY(),
                player.getZ(),
                stack
        );

        itemEntity.setToDefaultPickupDelay();

        if (!gameRules.getBoolean(KINGamerules.DEATH_DROPS_DESPAWN)) {
            itemEntity.setNeverDespawn();
        }

        float horizontalSpeed = MathHelper.nextFloat(random, 0, 0.2F);
        float angleRadians = random.nextFloat() * (float) (Math.PI * 2);

        itemEntity.setVelocity(
                -MathHelper.sin(angleRadians) * horizontalSpeed,
                0.2F,
                MathHelper.cos(angleRadians) * horizontalSpeed
        );

        world.spawnEntity(itemEntity);
    }

    private static boolean hasKeepInventory(ServerPlayerEntity player) {
        return hasKeepInventory(player.getEntityWorld());
    }

    private static boolean hasKeepInventory(ServerWorld world) {
        return world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
    }
}
