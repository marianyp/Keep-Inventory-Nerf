package dev.mariany.keepinventorynerf.logic;

import dev.mariany.keepinventorynerf.gamerule.KINGamerules;
import dev.mariany.keepinventorynerf.mixin.accessor.PlayerEntityAccessor;
import dev.mariany.keepinventorynerf.packet.clientbound.DeathLossesPacket;
import dev.mariany.keepinventorynerf.tag.KINTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

public final class DeathLossesHandler {
    private DeathLossesHandler() {
    }

    public static void dropAndNotify(ServerPlayer player) {
        notifyClient(player, getLostLevels(player), dropRandomItems(player));
    }

    private static void notifyClient(ServerPlayer player, int lostXp, List<ItemStack> droppedStacks) {
        ServerPlayNetworking.send(player, new DeathLossesPacket(lostXp, droppedStacks));
    }

    private static int getLostLevels(ServerPlayer player) {
        if (!hasKeepInventory(player)) {
            return 0;
        }

        return player.experienceLevel - ExperienceHandler.getKeptLevels(player);
    }

    private static List<ItemStack> dropRandomItems(ServerPlayer player) {
        ServerLevel level = player.level();

        if (!hasKeepInventory(level)) {
            return List.of();
        }

        return dropRandomItems(player, getRandomDropCount(level));
    }

    private static int getRandomDropCount(ServerLevel level) {
        GameRules gameRules = level.getGameRules();

        int minItemsToDrop = gameRules.get(KINGamerules.KEEP_INVENTORY_MIN_ITEMS_TO_DROP);
        int maxItemsToDrop = gameRules.get(KINGamerules.KEEP_INVENTORY_MAX_ITEMS_TO_DROP);

        return level.getRandom().nextIntBetweenInclusive(minItemsToDrop, maxItemsToDrop);
    }

    private static List<ItemStack> dropRandomItems(ServerPlayer player, int dropAmount) {
        RandomSource random = player.getRandom();
        Inventory inventory = player.getInventory();

        List<Integer> slotIndexes = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();

        ((PlayerEntityAccessor) player).keepinventorynerf$destroyVanishingCursedItems();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (!itemStack.isEmpty() && !itemStack.is(KINTags.Items.IGNORE_DROP)) {
                slotIndexes.add(i);
            }
        }

        for (int i = 0; i < dropAmount; i++) {
            if (slotIndexes.isEmpty() || inventory.isEmpty()) {
                break;
            }

            int slotToRemove = slotIndexes.remove(random.nextInt(slotIndexes.size()));

            ItemStack stack = inventory.getItem(slotToRemove);

            stacks.add(stack.copy());

            dropItem(player, stack.copy());

            inventory.removeItemNoUpdate(slotToRemove);
        }

        return stacks;
    }

    private static void dropItem(ServerPlayer player, ItemStack stack) {
        ServerLevel level = player.level();
        GameRules gameRules = level.getGameRules();
        RandomSource random = level.getRandom();

        ItemEntity itemEntity = new ItemEntity(
                level,
                player.getX(),
                player.getY(),
                player.getZ(),
                stack
        );

        itemEntity.setDefaultPickUpDelay();

        if (!gameRules.get(KINGamerules.DEATH_DROPS_DESPAWN)) {
            itemEntity.setUnlimitedLifetime();
        }

        float horizontalSpeed = Mth.nextFloat(random, 0, 0.2F);
        float angleRadians = random.nextFloat() * (float) (Math.PI * 2);

        itemEntity.setDeltaMovement(
                -Mth.sin(angleRadians) * horizontalSpeed,
                0.2F,
                Mth.cos(angleRadians) * horizontalSpeed
        );

        level.addFreshEntity(itemEntity);
    }

    private static boolean hasKeepInventory(ServerPlayer player) {
        return hasKeepInventory(player.level());
    }

    private static boolean hasKeepInventory(ServerLevel level) {
        return level.getGameRules().get(GameRules.KEEP_INVENTORY);
    }
}
