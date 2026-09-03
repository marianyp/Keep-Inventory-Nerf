package dev.mariany.keepinventorynerf.logic;

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
import net.minecraft.world.rule.GameRules;

import java.util.ArrayList;
import java.util.List;

public final class DeathLossesHandler {
    private DeathLossesHandler() {
    }

    public static void dropAndNotify(ServerPlayerEntity player) {
        notifyClient(player, getLostLevels(player), dropRandomItems(player));
    }

    private static void notifyClient(ServerPlayerEntity player, int lostXp, List<ItemStack> droppedStacks) {
        ServerPlayNetworking.send(player, new DeathLossesPacket(lostXp, droppedStacks));
    }

    private static int getLostLevels(ServerPlayerEntity player) {
        if (!hasKeepInventory(player)) {
            return 0;
        }

        return player.experienceLevel - ExperienceHandler.getKeptLevels(player);
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

        int minItemsToDrop = gameRules.getValue(KINGamerules.KEEP_INVENTORY_MIN_ITEMS_TO_DROP);
        int maxItemsToDrop = gameRules.getValue(KINGamerules.KEEP_INVENTORY_MAX_ITEMS_TO_DROP);

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

        if (!gameRules.getValue(KINGamerules.DEATH_DROPS_DESPAWN)) {
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
        return world.getGameRules().getValue(GameRules.KEEP_INVENTORY);
    }
}
